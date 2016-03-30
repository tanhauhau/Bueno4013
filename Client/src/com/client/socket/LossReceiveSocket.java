package com.client.socket;

import com.Console;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
import java.util.Random;

/**
 * Created by lhtan on 23/3/16.
 * LossReceiveSocket resemble the scenario where
 * there is a packet loss on receiving the packet
 */
public class LossReceiveSocket extends WrapperSocket {
    private final Random random;
    private final double prob;

    /**
     * Class Constructor for LossReceiveSocket
     *
     * @param socket      Socket Used
     * @param probability Probability of packet loss during receive, between 0 to 1
     */
    public LossReceiveSocket(Socket socket, double probability) {
        super(socket);
        this.random = new Random();
        this.prob = probability;
    }

    /**
     * This method will stimulate the scenario that
     * there are packet losses during the receiving to
     * test the at-most-one implementation especially
     * the request is non-idempotent
     *
     * @param p Datagram Packet
     * @throws IOException
     * @throws SocketTimeoutException
     */
    @Override
    public void receive(DatagramPacket p) throws IOException {
        if (random.nextDouble() < this.prob) {
            super.receive(p);                   /* Receive the packet if the prob of receiving is higher */
        } else {
            try {
                Thread.sleep(1000);             /* Else sleep the thread to stimulate packet loss */
                Console.info("  LossReceiveSocket >> Fake Packet Loss when receiving");
            } catch (InterruptedException ignored) {
            }
            throw new SocketTimeoutException();
        }
    }
}
