package com.subutai.socketrpc.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InstructionSocketClient extends InstructionSocket {

    private static final Logger LOGGER = Logger.getLogger(InstructionSocketClient.class.getName());
    private Socket socket;
    private InetAddress address;
    private int port;

    public InstructionSocketClient(InetAddress address, int port) {
        super(false);
        this.port = port;
        this.address = address;
    }

    @Override
    public void connect() throws IOException {
        LOGGER.log(Level.INFO, "Connection to : "+ address + ":" + port);
        this.socket = new Socket(address, port);
        LOGGER.log(Level.INFO, "Successfully connected to server");
        setSocket(this.socket);
    }

    @Override
    public void disconnect() throws IOException {
        LOGGER.log(Level.INFO, "Connection closing");
        this.socket.close();
        LOGGER.log(Level.INFO, "Connection closed");
    }
}
