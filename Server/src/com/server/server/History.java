package com.server.server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by lhtan on 23/3/16.
 * This is the History class used by the
 * AtMostOnceServer. This will store the result
 * of the request, in case of repeated
 * request, the servant will retrieve the result
 * from here and resend back to client
 */

public class History {

    private static final int HISTORY_SIZE = 10;
    private ArrayList<Client> clients;

    /**
     * Class Constructor for History
     */
    public History() {
        clients = new ArrayList<>();
    }

    /**
     * Retrieve the result from History based on the requestID
     * Will tranverse through the history to check whether is the
     * result is stored within it.
     * Return the result if found, else return null
     *
     * @param address       IP Address of Client
     * @param port          Port used for communication between server and client
     * @param requestId     Request ID
     * @return
     */
    public byte[] getFromHistory(InetAddress address, int port, long requestId){
        for (Client client : clients){
            if (client.match(address, port)){
                return client.checkHistory(requestId);
            }
        }
        return null;
    }

    /**
     * Save the result of the request into the History
     * @param address       IP Address of Client
     * @param port          Port used for communication between server and client
     * @param requestId     Request ID
     * @param response      Result from the execution of request
     */
    public void saveHistory(InetAddress address, int port, long requestId, byte[] response){
        Client c = null;
        for (Client client : clients){
            if (client.match(address, port)){
                c = client;
                break;
            }
        }
        if (c == null){
            c = new Client(address, port);
            clients.add(c);
        }
        c.saveHistory(requestId, response);
        return;
    }

    /**
     * This is the Client Class
     */
    public static class Client{
        private InetAddress address;
        private int port;
        private HashMap<Long, byte[]> history;
        private Long[] cycle;
        private int cycleIndex;

        /**
         * Class Constructor for Client Class
         * @param address       IP Address of Client
         * @param port          Port used for communication between server and client
         */
        public Client(InetAddress address, int port) {
            this.address = address;
            this.port = port;
            this.history = new HashMap<>();
            this.cycle = new Long[HISTORY_SIZE];
            Arrays.fill(this.cycle, -1L);
            this.cycleIndex = 0;
        }

        /**
         * Check both addresses and ports match or not
         * @param address       IP address of client
         * @param port          Port used for communication between server and client
         * @return
         */
        public boolean match(InetAddress address, int port){
            return address.equals(this.address) && port == this.port;
        }

        /**
         * return the byte array based on request ID if available
         * @param requestId     Request ID
         * @return
         */
        public byte[] checkHistory(long requestId){
            return history.get(requestId);
        }

        /**
         *
         * @param requestId     Request ID
         * @param response      Result from the execution of request
         */
        public void saveHistory(long requestId, byte[] response){
            if (cycle[cycleIndex] != -1){
                history.remove(cycle[cycleIndex]);
            }
            history.put(requestId, response);
            cycle[cycleIndex] = requestId;
            cycleIndex = (cycleIndex + 1) % HISTORY_SIZE;
        }

        /**
         * Clear the history and reset the cycle index
         */
        public void clear(){
            this.cycleIndex = 0;
            this.history.clear();
        }
    }
}
