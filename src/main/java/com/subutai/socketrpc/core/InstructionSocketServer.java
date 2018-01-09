package com.subutai.socketrpc.core;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InstructionSocketServer extends InstructionSocket {

    private static final Logger LOGGER = Logger.getLogger(InstructionSocketServer.class.getName());

    private int maxConnection = 1;
    private int port;
    private ServerSocket serverSocket;
    private Socket socket;

    public InstructionSocketServer(int port) {
        super(true);
        this.port = port;
    }

    @Override
    public void connect() throws IOException {
        LOGGER.log(Level.INFO, "Connection to port : " + port);
        this.serverSocket = ServerSocketFactory.getDefault().createServerSocket(port, maxConnection);
        this.socket = serverSocket.accept();
        LOGGER.log(Level.INFO, "Client successfully connected to server");
        setSocket(this.socket);
    }

    @Override
    public void disconnect() throws IOException{
        LOGGER.log(Level.INFO, "Connection closing");
        this.socket.close();
        this.serverSocket.close();
        LOGGER.log(Level.INFO, "Connection closed");
    }
}
