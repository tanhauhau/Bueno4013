package com.server.server;

import com.server.pack.Unpack;
import com.server.socket.*;
import com.server.strategy.ErrorStrategy;
import com.server.strategy.Strategy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by lhtan on 22/3/16.
 */
public class Server {
    //Port
    protected int mServerPort = 6789;
    protected static final int BUFFER_SIZE = 2048;
    protected static final byte EMPTY = 0;
    //UDP Socket
    protected Socket mSocket = null;

    //strategy
    protected HashMap<Integer, Strategy> strategy;
    protected ErrorStrategy errorStrategy;
    //buffer
    protected byte[] buffer = new byte[BUFFER_SIZE];

    public Server(int serverPort) throws SocketException {
        this.mServerPort = serverPort;
        this.mSocket = new NormalSocket(new DatagramSocket(this.mServerPort));
        this.strategy = new HashMap<>();
        this.errorStrategy = new ErrorStrategy();
    }

    public Server use(int requestCode, Strategy strategy){
        this.strategy.put(requestCode, strategy);
        return this;
    }

    /**
     *
     * @param prob probability of success sending out a packet
     * @return
     * @throws SocketException
     */
    public Server makeItPacketLossWhenSending(double prob) throws SocketException{
        this.mSocket = new LossSendSocket(this.mSocket, prob);
        return this;
    }
    /**
     * @param time time in ms lag when receiving and sending
     * @return
     * @throws SocketException
     */
    public Server makeItLag(int time) throws SocketException{
        this.mSocket = new LagSocket(this.mSocket, time);
        return this;
    }
    /**
     * @param prob probablitiy of not kisiao
     * @return
     * @throws SocketException
     */
    public Server makeItSendGibberish(double prob) throws SocketException{
        this.mSocket = new GibberishSocket(this.mSocket, prob);
        return this;
    }

    public void start() throws IOException {
        while (true) {
            DatagramPacket packet = receive();
            byte[] data = packet.getData();
            int request = data[0];
            Strategy handleStrategy = this.errorStrategy;
            if (this.strategy.containsKey(request)){
                handleStrategy = this.strategy.get(request);
            }

            Unpack.Result unpacked = handleStrategy.unpack(data);
            byte[] message = handleStrategy.handle(packet.getAddress(), packet.getPort(), mSocket, unpacked);
            this.mSocket.send(message, packet.getAddress(), packet.getPort());
        }
    }

    public void stop(){
        this.mSocket.close();
    }

    public DatagramPacket receive() throws IOException {
        cleanBuffer();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        this.mSocket.receive(packet);
        return packet;
    }

    protected void cleanBuffer() {
        Arrays.fill(buffer, EMPTY);
    }
}
