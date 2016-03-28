package com.server.server;

import com.server.pack.Unpack;
import com.server.strategy.Strategy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

/**
 * Created by lhtan on 23/3/16.
 */
/*
    This server implement at-most-one, where the request from client can be either
    idempotent or non-idempotent. There is a history that keep track of the recent requests
    in case of any packet loss or server lag, the server will retrieve the result from history,
    if the client happens to resend the request to server
 */
public class AtMostOnceServer extends Server {
    private History history;
    public AtMostOnceServer(int serverPort) throws SocketException {
        super(serverPort);
        history = new History();
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
            long requestId = unpacked.getLong(Strategy.REQUEST_ID);

            byte[] message = history.getFromHistory(packet.getAddress(), packet.getPort(), requestId);
            if (message == null) {
                message = handleStrategy.handle(packet.getAddress(), packet.getPort(), mSocket, unpacked);
                history.saveHistory(packet.getAddress(), packet.getPort(), requestId, message);
            }else{
                System.out.println(" AtMostOnceServer >> use history");
            }

            this.mSocket.send(message, packet.getAddress(), packet.getPort());
        }
    }
}
