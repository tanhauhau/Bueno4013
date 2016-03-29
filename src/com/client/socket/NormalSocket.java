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
     *
     * @param pack          Marshalling object
     * @param address       IP Address
     * @param port          Port used for communication between server and client
     * @throws IOException
     */
    public void send(Pack pack, InetAddress address, int port) throws IOException {
        Console.info(String.format(" Socket >> Packing %s", pack.toString()));
        byte[] message = pack.getByteArray();
        DatagramPacket packet = new DatagramPacket(message, message.length, address, port);
        send(packet);
    }

    /**
     * This method send the packed datagram packet to the correspond
     * IP address and port, together with the timestamp
     * @param p         Datagram Packet
     * @throws IOException
     */
    public void send(DatagramPacket p) throws IOException {
        Console.info(String.format(" Socket >> %s Sending packet to %s(%d)", new Date().toString(), p.getAddress().toString(), p.getPort()));
        this.socket.send(p);
    }

    /**
     * This method received datagram from client
     * @param p         Datagram Packet
     * @throws IOException
     */
    public void receive(DatagramPacket p) throws IOException{
        this.socket.receive(p);
        Console.info(String.format(" Socket >> %s Received packet from %s(%d)", new Date().toString(), p.getAddress().toString(), p.getPort()));
    }

    /**
     * This method close the connection socket
     */
    public void close(){
        Console.info(String.format(" Socket >> %s Closing socket", new Date().toString()));
        this.socket.close();
    }

    /**
     * Set timeout period for the timeout interval
     * @param time      Period of interval
     * @throws SocketException
     */
    @Override
    public void setTimeout(int time) throws SocketException {
        this.socket.setSoTimeout(time);
    }
}
