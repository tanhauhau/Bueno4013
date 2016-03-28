package com.server.server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by lhtan on 23/3/16.
 */

/*
    This is the history class implemented for at-most-one server
 */
public class History {

    private static final int HISTORY_SIZE = 10;
    private ArrayList<Client> clients;

    public History() {
        clients = new ArrayList<>();
    }

    public byte[] getFromHistory(InetAddress address, int port, long requestId){
        for (Client client : clients){
            if (client.match(address, port)){
                return client.checkHistory(requestId);
            }
        }
        return null;
    }

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

    public static class Client{
        private InetAddress address;
        private int port;
        private HashMap<Long, byte[]> history;
        private Long[] cycle;
        private int cycleIndex;

        public Client(InetAddress address, int port) {
            this.address = address;
            this.port = port;
            this.history = new HashMap<>();
            this.cycle = new Long[HISTORY_SIZE];
            Arrays.fill(this.cycle, -1L);
            this.cycleIndex = 0;
        }
        public boolean match(InetAddress address, int port){
            return address.equals(this.address) && port == this.port;
        }

        public byte[] checkHistory(long requestId){
            return history.get(requestId);
        }
        public void saveHistory(long requestId, byte[] response){
            if (cycle[cycleIndex] != -1){
                history.remove(cycle[cycleIndex]);
            }
            history.put(requestId, response);
            cycle[cycleIndex] = requestId;
            cycleIndex = (cycleIndex + 1) % HISTORY_SIZE;
        }
        public void clear(){
            this.cycleIndex = 0;
            this.history.clear();
        }
    }
}
