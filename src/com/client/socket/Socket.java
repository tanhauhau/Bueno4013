package com.client.socket;

import com.client.pack.Pack;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by lhtan on 24/3/16.
 * his Socket interface act as a template for the
 * daughter classes who implemented this interface
 */
public interface Socket {
    void send(Pack pack, InetAddress address, int port) throws IOException;
    void send(DatagramPacket p) throws IOException;
    void receive(DatagramPacket p) throws IOException;
    void close();
    void setTimeout(int time) throws SocketException;
}
