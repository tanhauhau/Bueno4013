package com.server.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Random;

/**
 * Created by lhtan on 23/3/16.
 * LossSendSocket class resemble packet loss
 * scenario during sending the packet
 */
public class LossSendSocket extends WrapperSocket {
    private final Random random;
    private final double prob;

    /**
     * Class Constructor for LossSendSocket
     * @param socket            Socket Used
     * @param probability       Probability of Packet Loss
     */
    public LossSendSocket(Socket socket, double probability) {
        super(socket);
        this.random = new Random();
        this.prob = probability;
    }

    /**
     * This method will stimulate the scenario that
     * there are packet losses during the sending to
     * test the at-most-one and also at least one
     * implementation especially
     *
     * @param data          Byte Array for communication
     * @param address       IP Address
     * @param port          Port used for communication between server and client
     * @throws IOException
     */
    @Override
    public void send(byte[] data, InetAddress address, int port) throws IOException {
        if (random.nextDouble() < this.prob){
            super.send(data, address, port);             /* Send the packet if the prob of sending is higher */
        }else{
            try {
                Thread.sleep(1000);                     /* Else sleep the thread to stimulate packet loss */
                System.out.println("  LossSendSocket >> Fake Packet Loss when sending");
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void send(DatagramPacket p) throws IOException {
        if (random.nextDouble() < this.prob){
            super.send(p);                               /* Send the packet if the prob of sending is higher */
        }else{
            try {
                Thread.sleep(1000);                      /* Else sleep the thread to stimulate packet loss */
                System.out.println("  LossSendSocket >> Fake Packet Loss when sending");
            } catch (InterruptedException e) {
            }
        }
    }
}
