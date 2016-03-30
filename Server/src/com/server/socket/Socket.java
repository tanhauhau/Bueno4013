package com.server.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created by lhtan on 24/3/16.
 * This Socket interface act as a template for the
 * daughter classes who implemented this interface
 */
public interface Socket {
    void send(byte[] data, InetAddress address, int port) throws IOException;
    void send(DatagramPacket p) throws IOException;
    void receive(DatagramPacket p) throws IOException;
    void close();
}
