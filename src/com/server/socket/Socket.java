package com.server.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created by lhtan on 24/3/16.
 */
public interface Socket {
    void send(byte[] data, InetAddress address, int port) throws IOException;
    void send(DatagramPacket p) throws IOException;
    void receive(DatagramPacket p) throws IOException;
    void close();
}
