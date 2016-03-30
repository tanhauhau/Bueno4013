package com.server.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

/**
 * Created by lhtan on 22/3/16.
 * This NormalSocket class is the normal socket class,
 * which without any alters on the performance
 */
public class NormalSocket implements Socket {
    private DatagramSocket socket;

    /**
     * Class Constructor for NormalSocket
     * @param socket        Socket used
     */
    public NormalSocket(DatagramSocket socket){
        this.socket = socket;
    }

    /**
     * This method create a new datagram packet with the parameters input and
     * send the packet
     * @param data          Byte Array for communication
     * @param address       IP Address
     * @param port          Port used for communication between server and client
     * @throws IOException
     */
    public void send(byte[] data, InetAddress address, int port) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        send(packet);
    }

    /**
     * This method send the packed datagram packet to the correspond
     * IP address and port, together with the timestamp
     * @param p         Datagram Packet
     * @throws IOException
     */
    public void send(DatagramPacket p) throws IOException {
        System.out.println(String.format(" Socket >> %s Sending packet to %s(%d)", new Date().toString(), p.getAddress().toString(), p.getPort()));
        this.socket.send(p);
    }

    /**
     * This method received datagram from client
     * @param p         Datagram Packet
     * @throws IOException
     */
    public void receive(DatagramPacket p) throws IOException{
        this.socket.receive(p);
        System.out.println(String.format(" Socket >> %s Received packet from %s(%d)", new Date().toString(), p.getAddress().toString(), p.getPort()));
    }

    /**
     * This method close the connection socket
     */
    public void close(){
        System.out.println(String.format(" Socket >> %s Closing socket", new Date().toString()));
        this.socket.close();
    }
}
