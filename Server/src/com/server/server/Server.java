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
 * This Server Class resemble the typical server
 * which has the policy of at-least-one, where
 * all requests are assumed idempotent
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

    /**
     * Class Constructor for Server
     * @param serverPort    Port used for communication between server and client
     * @throws SocketException
     */
    public Server(int serverPort) throws SocketException {
        this.mServerPort = serverPort;
        this.mSocket = new NormalSocket(new DatagramSocket(this.mServerPort));
        this.strategy = new HashMap<>();
        this.errorStrategy = new ErrorStrategy();
    }

    /**
     *
     * @param requestCode       The integer acts as key for the correspond Strategy services
     * @param strategy          The Strategy services
     * @return
     */
    public Server use(int requestCode, Strategy strategy){
        this.strategy.put(requestCode, strategy);
        return this;
    }

    /**
     * Enable Packet Loss scenario during sending
     * @param prob probability of success sending out a packet
     * @return
     * @throws SocketException
     */
    public Server makeItPacketLossWhenSending(double prob) throws SocketException{
        this.mSocket = new LossSendSocket(this.mSocket, prob);
        return this;
    }
    /**
     * Enable Server Lag scenario
     * @param time time in ms lag when receiving and sending
     * @return
     * @throws SocketException
     */
    public Server makeItLag(int time) throws SocketException{
        this.mSocket = new LagSocket(this.mSocket, time);
        return this;
    }
    /**
     * Enable corrupted and damaged packet during transmission
     * @param prob probablitiy of packet not damaged
     * @return
     * @throws SocketException
     */
    public Server makeItSendGibberish(double prob) throws SocketException{
        this.mSocket = new GibberishSocket(this.mSocket, prob);
        return this;
    }

    /**
     * Start the Server
     * @throws IOException
     */
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

    /**
     * Close the connection socket
     */
    public void stop(){
        this.mSocket.close();
    }

    /**
     * Receive Datagram packet from transmission
     * @return  packet received in communication
     * @throws IOException
     */
    public DatagramPacket receive() throws IOException {
        cleanBuffer();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        this.mSocket.receive(packet);
        return packet;
    }

    /**
     * Clean the buffer used to receive datagram packet
     */
    protected void cleanBuffer() {
        Arrays.fill(buffer, EMPTY);
    }
}
