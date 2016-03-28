package com.client.strategy;

import com.Console;
import com.client.Client;
import com.client.pack.OneByteInt;
import com.client.pack.Pack;
import com.client.pack.Unpack;

import java.io.IOException;

/**
 * Created by lhtan on 23/3/16.
 */
public class PingStrategy extends Strategy {

    private final static String DATA = "data";

    public PingStrategy() {
        super(new Unpack.Builder()
                .setType(DATA, Unpack.TYPE.STRING)
                .build());
    }

    /*
        Accept and wrap the user request
        Marshall the request into byte array and
        sent to server for further services.
     */
    @Override
    public void serviceUser(Console scanner, Client client) throws IOException {
        String message = scanner.askForString("Ping what?");
        long messageId = client.getMessageId();
        Pack ping = new Pack.Builder()
                .setValue("request", new OneByteInt(Client.PING_REQUEST))
                .setValue("id", messageId)
                .setValue("message", message)
                .build();
        client.send(ping);

        /*
        DatagramPacket packet = client.receive();
        Unpack.Result result = unpack(packet.getData());
        if (isStatusOK(result)){
            System.out.println(String.format("Received : %s", result.getString(DATA)));
        }
        */

        Unpack.Result result = keepTryingUntilReceive(client, ping, messageId);
        if (isStatusOK(result)) {
            Console.println(String.format("Received : %s", result.getString(DATA)));
        }else{
            Console.println("Failed");
        }

    }

    @Override
    public String getTitle() {
        return "Ping Server";
    }
}
