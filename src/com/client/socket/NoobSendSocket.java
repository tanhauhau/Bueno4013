package com.client.socket;

import com.Console;
import com.client.pack.Pack;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Random;

/**
 * Created by lhtan on 23/3/16.
 */
public class NoobSendSocket extends WrapperSocket {
    private final Random random;
    private final double prob;
    public NoobSendSocket(Socket socket, double probability) {
        super(socket);
        this.random = new Random();
        this.prob = probability;
    }

    @Override
    public void send(Pack pack, InetAddress address, int port) throws IOException {
        if (random.nextDouble() < this.prob){
            super.send(pack, address, port);
        }else{
            //sleep for a while, or else too fast
            try {
                Thread.sleep(1000);
                Console.info("  NoobSendSocket >> Fake Packet Loss when sending");
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
                Console.info("  NoobSendSocket >> Fake Packet Loss when sending");
            } catch (InterruptedException e) {
            }
        }
    }
}
