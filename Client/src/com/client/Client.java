package com.client;

import com.Console;
import com.client.pack.Pack;
import com.client.socket.*;
import com.client.socket.Socket;
import com.client.strategy.Strategy;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * This is the main Client class in the client-server
 */

public class Client {
    public static final int PING_REQUEST = 0;
    public static final int READ_REQUEST = 1;
    public static final int WRITE_REQUEST = 2;
    public static final int REGISTER_REQUEST = 3;
    public static final int SIZE_REQUEST = 4;
    public static final int DOUBLE_REQUEST = 5;
    public static final int LAST_MODIFIED_REQUEST = 6;
    public static final int NEW_FILE_REQUEST = 7;
    private static final int BUFFER_SIZE = 1048576; //1MB
    private static final byte EMPTY = 0;
    //buffer
    private byte[] buffer = new byte[BUFFER_SIZE];
    //Address and Port
    private String mServerIPAddress;
    private int mServerPort;
    //UDP Socket
    private Socket mSocket = null;
    private InetAddress mServerAddress = null;
    private int mTimeout = 0;
    //strategy
    private HashMap<Integer, Strategy> strategy;
    private long messageId = 0;

    /**
     * File Constructor of Client Class
     *
     * @param serverIPAddress Server IP Address
     * @param serverPort      Server Port for communication
     * @param timeout         timeout for communication
     * @throws UnknownHostException
     * @throws SocketException
     */
    public Client(String serverIPAddress, int serverPort, int timeout) throws UnknownHostException, SocketException {
        this.mServerIPAddress = serverIPAddress;
        this.mServerPort = serverPort;
        this.mServerAddress = InetAddress.getByName(mServerIPAddress);
        this.strategy = new HashMap<>();
        this.mTimeout = timeout;
        this.mSocket = new NormalSocket(new DatagramSocket());
        this.mSocket.setTimeout(this.mTimeout);
    }

    /**
     * Store the request ID and the Strategy class into a hashmap
     *
     * @param requestCode Request ID
     * @param strategy    Strategy Class
     * @return Client object itself
     */
    public Client use(int requestCode, Strategy strategy) {
        this.strategy.put(requestCode, strategy);
        return this;
    }

    /**
     * This method resemble the scenario where packet loss
     * during sending in communication with server
     *
     * @param prob probability of success sending out a packet
     * @return Client object itself
     * @throws SocketException
     */

    public Client makeItPacketLossWhenSending(double prob) throws SocketException {
        this.mSocket = new LossSendSocket(this.mSocket, prob);
        return this;
    }

    /**
     * This method resemble the scenario where packet loss
     * during receiving in communication with server
     *
     * @param prob probability of success sending out a packet
     * @return Client object itself
     * @throws SocketException
     */
    public Client makeItPacketLossWhenReceiving(double prob) throws SocketException {
        this.mSocket = new LossReceiveSocket(this.mSocket, prob);
        return this;
    }

    /**
     * This method resemble the scenario of Server Lag
     * during communication with server
     *
     * @param time time in ms lag when receiving and sending
     * @return Client object itself
     * @throws SocketException
     */
    public Client makeItLag(int time) throws SocketException {
        this.mSocket = new LagSocket(this.mSocket, time);
        return this;
    }

    /**
     * This method resemble the scenario of damaged and corrupted packets
     * during communication with server
     *
     * @param prob probablitiy of damaged and corrupted packet
     * @return Client object itself
     * @throws SocketException
     */
    public Client makeItSendGibberish(double prob) throws SocketException {
        this.mSocket = new GibberishSocket(this.mSocket, prob);
        return this;
    }

    /**
     * This method will print out the menu of services available
     */
    public void printMenu() {
        for (Integer option : strategy.keySet()) {
            Console.println(String.format("%d. %s", option, strategy.get(option).getTitle()));
        }
    }

    /**
     * Stop the socket connection
     */
    public void stop() {
        mSocket.close();
    }

    /**
     * This method will start the execution of the Strategy services
     * provided by the server for Clients
     *
     * @param option  Option of the strategy provided
     * @param console The console object
     * @throws IOException
     */
    public void execute(int option, Console console) throws IOException {
        if (this.strategy.containsKey(option)) {
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

    public void send(Pack request) throws IOException {
        this.mSocket.send(request, mServerAddress, mServerPort);
    }

    /**
     * Receive datagram packet from Server
     *
     * @return datagram packet
     * @throws IOException
     */
    public DatagramPacket receive() throws IOException {
        cleanBuffer();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        this.mSocket.receive(packet);
        return packet;
    }

    private void cleanBuffer() {
        Arrays.fill(buffer, EMPTY);
    }

    public long getMessageId() {
        return messageId++;
    }
}
