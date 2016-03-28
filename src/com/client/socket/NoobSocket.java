package com.client.socket;

import com.Console;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.Random;

/**
 * Created by lhtan on 23/3/16.
 */
public class NoobSocket extends NormalSocket {
    private final Random random;
    public NoobSocket(DatagramSocket socket) {
        super(socket);
        random = new Random();
    }

    @Override
    public void send(DatagramPacket p) throws IOException {
        //5% of packet loss, simulating at most once
        if (random.nextInt(20) < 19){
            super.send(p);
        }else{
            //sleep for a while, or else too fast
            try {
                Thread.sleep(1000);
                Console.info(" NoobSocket >> Fake Packet Loss when sending");
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void receive(DatagramPacket p) throws IOException, SocketTimeoutException {
        //5% of packet loss, simulating at most once
        if (random.nextInt(20) < 19){
            super.receive(p);
        }else{
            //sleep for a while, or else too fast
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            throw new SocketTimeoutException();
        }
    }
}
