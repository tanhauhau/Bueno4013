package com.client.socket;

import com.Console;
import com.client.pack.Pack;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

/**
 * Created by lhtan on 22/3/16.
 */
public class NormalSocket implements Socket {
    private DatagramSocket socket;
    public NormalSocket(DatagramSocket socket){
        this.socket = socket;
    }
    public void send(Pack pack, InetAddress address, int port) throws IOException {
        Console.info(String.format(" Socket >> Packing %s", pack.toString()));
        byte[] message = pack.getByteArray();
        DatagramPacket packet = new DatagramPacket(message, message.length, address, port);
        send(packet);
    }
    public void send(DatagramPacket p) throws IOException {
        Console.info(String.format(" Socket >> %s Sending packet to %s(%d)", new Date().toString(), p.getAddress().toString(), p.getPort()));
        this.socket.send(p);
    }
    public void receive(DatagramPacket p) throws IOException{
        this.socket.receive(p);
        Console.info(String.format(" Socket >> %s Received packet from %s(%d)", new Date().toString(), p.getAddress().toString(), p.getPort()));
    }
    public void close(){
        Console.info(String.format(" Socket >> %s Closing socket", new Date().toString()));
        this.socket.close();
    }

    @Override
    public void setTimeout(int time) throws SocketException {
        this.socket.setSoTimeout(time);
    }
}
