package com.server.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created by lhtan on 24/3/16.
 * This WrapperSocket class act as a pre-initialized
 * class. Daughter class who inherited this class
 * will have to override certain functions which it
 * required
 */
public class WrapperSocket implements Socket {

    private final Socket socket;

    public WrapperSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void send(byte[] data, InetAddress address, int port) throws IOException {
        this.socket.send(data, address, port);
    }

    @Override
    public void send(DatagramPacket p) throws IOException {
        this.socket.send(p);
    }

    @Override
    public void receive(DatagramPacket p) throws IOException {
        this.socket.receive(p);
    }

    @Override
    public void close() {
        this.socket.close();
    }
}
