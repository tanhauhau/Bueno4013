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

    public GibberishSocket(Socket socket, double probability) {
        super(socket);
        this.random = new Random();
        this.prob = probability;
    }

    @Override
    public void send(byte[] data, InetAddress address, int port) throws IOException {
        if (isSoSuay()) fillRubbish(data);
        super.send(data, address, port);
    }

    @Override
    public void send(DatagramPacket p) throws IOException {
        if (isSoSuay()) fillRubbish(p.getData());
        super.send(p);
    }

    @Override
    public void receive(DatagramPacket p) throws IOException {
        super.receive(p);
        if (isSoSuay()) fillRubbish(p.getData());
    }

    private void fillRubbish(byte[] data) {
        this.random.nextBytes(data);
    }

    private boolean isSoSuay(){
        return this.random.nextDouble() >= this.prob;
    }
}
