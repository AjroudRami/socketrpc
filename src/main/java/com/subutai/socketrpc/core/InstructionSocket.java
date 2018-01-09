package com.subutai.socketrpc.core;
import com.subutai.socketrpc.Operation;
import com.subutai.socketrpc.OperationResponse;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class InstructionSocket implements Runnable {

    //State variables
    private boolean stop = false;
    private boolean pause = false;
    private boolean connected = false;
    private boolean reconnectOnFailure = true;
    private boolean receiver = true;
    private int nbFailure;

    //Listener on new message
    private OperationListener operationListener;
    private ResponseListener responseListener;

    //IO
    private InputStream in;
    private OutputStream out;
    ObjectInputStream oIn;
    ObjectOutputStream oOut;
    private Socket socket;

    private static final Logger LOGGER = Logger.getLogger(InstructionSocket.class.getSimpleName());

    public InstructionSocket(boolean receiver) {
        this.receiver = receiver;
    }

    public synchronized void sendOperation(Operation instruction){
        try {
            oOut.writeObject(instruction);
            oOut.flush();
        } catch (IOException e) {
            failure(e);
        }
    }
    private void receive() {
        try {
            if (operationListener != null && !this.socket.isClosed()) {
                Object object = oIn.readObject();
                handleReception(object);
            }
        } catch (EOFException e) {
            stop();
        } catch (IOException e) {
            failure(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized void setOperationListener(OperationListener operationListener) {
        this.operationListener = operationListener;
    }

    public synchronized void setResponseListener(ResponseListener responseListener){
        this.responseListener = responseListener;
    }

    private void handleReception(Object object) {
        if (object instanceof Operation) {
            operationListener.onOperationReceived((Operation) object);
        } else if (object instanceof OperationResponse) {

        }
    }
    public void run() {
        LOGGER.log(Level.INFO, "Start Thread");
        while (!stop) {
            runSocketLoop();
        }
        try {
            disconnect();
        } catch (IOException e) {
            failure(e);
        }
        LOGGER.log(Level.INFO, "End Thread");
    }

    public void runLoop(){
        if (!stop) {
            LOGGER.log(Level.INFO, "STOP : " + stop);
            runSocketLoop();
        } else {
            try {
                disconnect();
            } catch (IOException e) {
                failure(e);
            }
        }
    }

    private void runSocketLoop(){
        if(!pause) {
            connectIfNot();
            receive();
        }
    }

    private void connectIfNot(){
        if (!connected && !(nbFailure != 0 && !reconnectOnFailure)) {
            try {
                connect();
                bindIO();
            } catch (IOException e) {
                failure(e);
            }
            connected = true;
        } else if (connected) {
            return;
        } else {
            LOGGER.log(Level.INFO, "Server stop = true");
            stop = true;
        }
    }

    public abstract void connect() throws IOException;

    public abstract void disconnect() throws IOException;

    public void closeStreams(){
        Closeable[] streams = new Closeable[] {oOut, oIn, out, in};
        for (int i = 0; i < streams.length; i++) {
            try{
                streams[i].close();
            } catch (IOException e) {
                failure(e);
            }
        }
    }

    private void bindIO(){
        try {
            LOGGER.log(Level.INFO, "Binding streams");
            LOGGER.log(Level.INFO, "Binding InputStream");
            this.in = socket.getInputStream();
            LOGGER.log(Level.INFO, "Binding OutputStream");
            this.out = socket.getOutputStream();
            if (receiver) {
                LOGGER.log(Level.INFO, "Binding ObjectOutputStream");
                oOut = new ObjectOutputStream(out);
                LOGGER.log(Level.INFO, "Binding ObjectInputStream");
                oIn = new ObjectInputStream(in);
            } else {
                LOGGER.log(Level.INFO, "Binding ObjectInputStream");
                oIn = new ObjectInputStream(in);
                LOGGER.log(Level.INFO, "Binding ObjectOutputStream");
                oOut = new ObjectOutputStream(out);
            }
            LOGGER.log(Level.INFO, "Binding streams success");
        } catch (IOException e) {
            failure(e);
        }
    }

    private void failure(Exception e) {
        nbFailure ++;
        LOGGER.log(Level.WARNING, "Socket connection failure caused by: \n" + e.getMessage());
        e.printStackTrace();
        LOGGER.log(Level.WARNING, "Total failures: " + nbFailure);
    }

    public synchronized void stop() {
        LOGGER.log(Level.INFO, "Instruction socket stopped");
        this.stop = true;
        try {
            disconnect();
        } catch (IOException e) {
            failure(e);
        }
    }

    public synchronized void resume() {
        LOGGER.log(Level.INFO, "Instruction socket resumed");
        this.pause = false;
    }

    public synchronized void pause() {
        LOGGER.log(Level.INFO, "Instruction socket paused");
        this.pause = true;
    }

    void setSocket(Socket socket) {
        this.socket = socket;
    }

    public boolean running() {
        return !this.stop;
    }

    public InetAddress getAddress() {
        return this.socket.getInetAddress();
    }
}
