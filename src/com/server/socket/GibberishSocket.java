package com.server.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Random;

/**
 * Created by lhtan on 24/3/16.
 */
public class GibberishSocket extends WrapperSocket {
    private final Random random;
    private final double prob;

    /**
     * Class Constructor for GibberishSocket
     * @param socket            Socket used for communication
     * @param probability       Probability of the packet will be damage and corrupted
     */
    public GibberishSocket(Socket socket, double probability) {
        super(socket);
        this.random = new Random();
        this.prob = probability;
    }

    /**
     * Fill the bytearray in datagram with rubbish data
     * @param data              Byte Array in the datagram packet
     * @param address           IP Address
     * @param port              Port used for communication between server and client
     * @throws IOException
     */
    @Override
    public void send(byte[] data, InetAddress address, int port) throws IOException {
        if (isSoUnlucky()) fillRubbish(data);
        super.send(data, address, port);
    }

    /**
     * Fill the bytearray in datagram with rubbish data
     * @param p                 Datagram Packet
     * @throws IOException
     */
    @Override
    public void send(DatagramPacket p) throws IOException {
        if (isSoUnlucky()) fillRubbish(p.getData());
        super.send(p);
    }


    @Override
    public void receive(DatagramPacket p) throws IOException {
        super.receive(p);
        if (isSoUnlucky()) fillRubbish(p.getData());
    }

    /**
     * Pack the byte array with random data
     * @param data              Byte Array in Datagram Packet
     */
    private void fillRubbish(byte[] data) {
        this.random.nextBytes(data);
    }

    /**
     * This method will utilize a random double generator, to compare with
     * the probability set by the client, such as to create a pseudo probability
     * of the socket will experience packet loss
     *
     * @return A boolean of either true of false
     */
    private boolean isSoUnlucky(){
        return this.random.nextDouble() >= this.prob;
    }
}
