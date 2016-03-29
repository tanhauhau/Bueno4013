package com.client.socket;

import com.Console;
import com.client.pack.Pack;

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
     * @param socket            Socket Used for communication
     * @param probability       Probability of Packet Loss during Sending
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
     * @param pack              Marshalling object
     * @param address           IP Address
     * @param port              Port used for communication between server and client
     * @throws IOException
     */
    @Override
    public void send(Pack pack, InetAddress address, int port) throws IOException {
        if (random.nextDouble() < this.prob){
            super.send(pack, address, port);
        }else{
            //sleep for a while, or else too fast
            try {
                Thread.sleep(1000);
                Console.info("  LossSendSocket >> Fake Packet Loss when sending");
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void send(DatagramPacket p) throws IOException {
        if (random.nextDouble() < this.prob){
            super.send(p);
        }else{
            //sleep for a while, or else too fast
            try {
                Thread.sleep(1000);
                Console.info("  LossSendSocket >> Fake Packet Loss when sending");
            } catch (InterruptedException e) {
            }
        }
    }
}
