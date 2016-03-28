package com.client.socket;

import com.client.pack.Pack;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created by lhtan on 24/3/16.
 */
public class LagSocket extends WrapperSocket {
    private final int time;

    /**
     * @param time lag for this number of millisecond when sending or receiving packet
     */
    public LagSocket(Socket socket, int time) {
        super(socket);
        this.time = time;
    }

    @Override
    public void send(Pack pack, InetAddress address, int port) throws IOException {
        lag();
        super.send(pack, address, port);
    }

    @Override
    public void send(DatagramPacket p) throws IOException {
        lag();
        super.send(p);
    }

    @Override
    public void receive(DatagramPacket p) throws IOException {
        super.receive(p);
        lag();
    }

    private void lag(){
        try {
            Thread.sleep(this.time);
        } catch (InterruptedException e) {
        }
    }
}
