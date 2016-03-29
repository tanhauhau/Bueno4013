package com.client.socket;

import com.client.pack.Pack;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created by lhtan on 24/3/16.
 * This LagSocket class will resemble server lag scenario,
 * with a pre-defined lag
 */
public class LagSocket extends WrapperSocket {
    private final int time;

    /**
     * Class Constructor for LagSocket
     * @param time lag for this number of millisecond when sending or receiving packet
     */
    public LagSocket(Socket socket, int time) {
        super(socket);
        this.time = time;
    }

    /**
     * This method create a new datagram packet with the parameters input and
     * send the packet, with a lag pre-defined
     * @param pack          Marshalling object
     * @param address       IP Address
     * @param port          Port used for communication between server and client
     * @throws IOException
     */
    @Override
    public void send(Pack pack, InetAddress address, int port) throws IOException {
        lag();
        super.send(pack, address, port);
    }

    /**
     * This method create a new datagram packet with the parameters input and
     * send the packet, with a lag pre-defined
     * @param p             Datagram Packet
     * @throws IOException
     */
    @Override
    public void send(DatagramPacket p) throws IOException {
        lag();
        super.send(p);
    }

    /**
     * This method received datagram from client
     * @param p             Datagram Packet
     * @throws IOException
     */
    @Override
    public void receive(DatagramPacket p) throws IOException {
        super.receive(p);
        lag();
    }

    /**
     * This method will make the thread sleep for
     * the pre-defined time, to resemble the scenario
     * of server lag
     */
    private void lag(){
        try {
            Thread.sleep(this.time);
        } catch (InterruptedException e) {
        }
    }
}
