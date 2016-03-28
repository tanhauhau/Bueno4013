package com.client;

import com.Console;
import com.client.strategy.Strategy;
import com.client.pack.Pack;
import com.client.socket.*;
import com.client.socket.Socket;
import com.sun.tools.javac.util.Assert;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;

public class Client {
    public static final int PING_REQUEST = 0;
    public static final int READ_REQUEST = 1;
    public static final int WRITE_REQUEST = 2;
    public static final int REGISTER_REQUEST = 3;
    public static final int SIZE_REQUEST = 4;
    public static final int DOUBLE_REQUEST = 5;
    public static final int LAST_MODIFIED_REQUEST = 6;

    //Address and Port
    private String mServerIPAddress = "192.168.0.255";
    private int mServerPort = 6789;
    private static final int BUFFER_SIZE = 1048576; //1MB
    private static final byte EMPTY = 0;

    //UDP Socket
    private Socket mSocket = null;
    private InetAddress mServerAddress = null;
    private int mTimeout = 0;

    //strategy
    private HashMap<Integer, Strategy> strategy;

    //buffer
    byte[] buffer = new byte[BUFFER_SIZE];

    private long messageId = 0;

    public Client(String serverIPAddress, int serverPort, int timeout) throws UnknownHostException, SocketException {
        this.mServerIPAddress = serverIPAddress;
        this.mServerPort = serverPort;
        this.mServerAddress = InetAddress.getByName(mServerIPAddress);
        this.strategy = new HashMap<>();
        this.mTimeout = timeout;
        this.mSocket = new NormalSocket(new DatagramSocket());
        this.mSocket.setTimeout(this.mTimeout);
    }

    public Client use(int requestCode, Strategy strategy){
        this.strategy.put(requestCode, strategy);
        return this;
    }

    /**
     *
     * @param prob probability of success sending out a packet
     * @return
     * @throws SocketException
     */
    public Client makeItPacketLossWhenSending(double prob) throws SocketException{
        this.mSocket = new LossSendSocket(this.mSocket, prob);
        return this;
    }
    /**
     *
     * @param prob probability of success sending out a packet
     * @return
     * @throws SocketException
     */
    public Client makeItPacketLossWhenReceiving(double prob) throws SocketException{
        this.mSocket = new LossReceiveSocket(this.mSocket, prob);
        return this;
    }
    /**
     * @param time time in ms lag when receiving and sending
     * @return
     * @throws SocketException
     */
    public Client makeItLag(int time) throws SocketException{
        this.mSocket = new LagSocket(this.mSocket, time);
        return this;
    }
    /**
     * @param prob probablitiy of not kisiao
     * @return
     * @throws SocketException
     */
    public Client makeItSendGibberish(double prob) throws SocketException{
        this.mSocket = new GibberishSocket(this.mSocket, prob);
        return this;
    }

    public void printMenu(){
        for (Integer option: strategy.keySet()) {
            Console.println(String.format("%d. %s", option, strategy.get(option).getTitle()));
        }
    }

    public void stop(){
        Assert.checkNonNull(mSocket, "Socket hasn't instantiated, you mother strategy..");
        mSocket.close();
    }

    public void execute(int option, Console console) throws IOException {
        if (this.strategy.containsKey(option)){
            Strategy strategy = this.strategy.get(option);
            strategy.serviceUser(console, this);
        }
    }

    public void setTimeout(int timeout) throws SocketException {
        this.mSocket.setTimeout(timeout);
    }

    public void resetTimeout() throws SocketException {
        this.mSocket.setTimeout(this.mTimeout);
    }

    public void send(Pack request) throws IOException{
        this.mSocket.send(request, mServerAddress, mServerPort);
    }

    public DatagramPacket receive() throws IOException {
        cleanBuffer();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        this.mSocket.receive(packet);
        return packet;
    }

    private void cleanBuffer() {
        Arrays.fill(buffer, EMPTY);
    }
    public long getMessageId(){
        return messageId ++;
    }
}
