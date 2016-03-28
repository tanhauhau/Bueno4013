package com.client.socket;

import com.Console;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
import java.util.Random;

/**
 * Created by lhtan on 23/3/16.
 */
public class NoobReceiveSocket extends WrapperSocket {
    private final Random random;
    private final double prob;
    public NoobReceiveSocket(Socket socket, double probability) {
        super(socket);
        this.random = new Random();
        this.prob = probability;
    }

    @Override
    public void receive(DatagramPacket p) throws IOException, SocketTimeoutException {
        //5% of packet loss, simulating at most once
        if (random.nextDouble() < this.prob){
            super.receive(p);
        }else{
            //sleep for a while, or else too fast
            try {
                Thread.sleep(1000);
                Console.info("  NoobReceiveSocket >> Fake Packet Loss when receiving");
            } catch (InterruptedException e) {
            }
            throw new SocketTimeoutException();
        }
    }
}
