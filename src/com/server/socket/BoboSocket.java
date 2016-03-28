package com.server.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

/**
 * Created by lhtan on 22/3/16.
 */
public class BoboSocket implements Socket {
    private DatagramSocket socket;
    public BoboSocket(DatagramSocket socket){
        this.socket = socket;
    }
    public void send(byte[] data, InetAddress address, int port) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        send(packet);
    }
    public void send(DatagramPacket p) throws IOException {
        System.out.println(String.format(" Socket >> %s Sending packet to %s(%d)", new Date().toString(), p.getAddress().toString(), p.getPort()));
        this.socket.send(p);
    }
    public void receive(DatagramPacket p) throws IOException{
        this.socket.receive(p);
        System.out.println(String.format(" Socket >> %s Received packet from %s(%d)", new Date().toString(), p.getAddress().toString(), p.getPort()));
    }
    public void close(){
        System.out.println(String.format(" Socket >> %s Closing socket", new Date().toString()));
        this.socket.close();
    }
}
