package com.server.callback;

import com.server.pack.OneByteInt;
import com.server.pack.Pack;
import com.server.socket.Socket;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lhtan on 23/3/16.
 */
public class Callback {

    private HashMap<String, ArrayList<Busybody>> aGroupOfKepos;
    private String folder;
    public Callback(String folder) {
        aGroupOfKepos = new HashMap<>();
        this.folder = folder;
    }

    public void register(String filename, int interval, long id, InetAddress address, int port){
        long expiry = System.currentTimeMillis() + interval * 1000;
        Busybody busybody = new Busybody(address, port, id, expiry);
        if (!aGroupOfKepos.containsKey(filename)){
            aGroupOfKepos.put(filename, new ArrayList<Busybody>());
        }
        ArrayList<Busybody> busybodies = aGroupOfKepos.get(filename);
        busybodies.add(busybody);
    }

    public void inform(String filename, Socket socket) throws IOException{
        long currentTime = System.currentTimeMillis();
        ArrayList<Busybody> busybodies = aGroupOfKepos.get(filename);
        if (busybodies != null){
            byte[] data = Files.readAllBytes(Paths.get(folder, filename));

            ArrayList<Busybody> expired = new ArrayList<>();
            for (Busybody busybody : busybodies){
                if (busybody.isExpired(currentTime)){
                    expired.add(busybody);
                }else{
                    System.out.println(String.format("   Callback >> inform %s(%d)", busybody.getAddress().toString(), busybody.getPort()));
                    byte[] message = new Pack.Builder()
                            .setValue("status", new OneByteInt(0))
                            .setValue("id", busybody.getId())
                            .setValue("dataStatus", new OneByteInt(0b01010101))
                            .setValue("data", data)
                            .setValue("dataStatus2", new OneByteInt(0b10101010))
                            .build()
                            .getByteArray();
                    socket.send(message, busybody.getAddress(), busybody.getPort());
                }
            }
            //clear
            System.out.println(String.format("   Callback >> remove %d busybody", expired.size()));
            busybodies.removeAll(expired);
            expired.clear();
        }
    }

    private class Busybody{
        private InetAddress address;
        private int port;
        private long expiry;
        private long id;
        public Busybody(InetAddress address, int port, long id, long expiry) {
            this.address = address;
            this.port = port;
            this.expiry = expiry;
            this.id = id;
        }
        public boolean isExpired(long time){
            return expiry < time;
        }

        public InetAddress getAddress() {
            return address;
        }

        public int getPort() {
            return port;
        }

        public long getId() {
            return id;
        }
    }
}
