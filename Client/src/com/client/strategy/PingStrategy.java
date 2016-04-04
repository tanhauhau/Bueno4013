package com.client.strategy;

import com.Console;
import com.client.Client;
import com.client.pack.OneByteInt;
import com.client.pack.Pack;
import com.client.pack.Unpack;

import java.io.IOException;

/**
 * Created by lhtan on 23/3/16.
 * This Class enable Client to ping the server
 * If the ping is success, the content sent by client
 * will be resend back by Server
 */
public class PingStrategy extends Strategy {

    private final static String DATA = "data";

    /**
     * Class Constructor for PingStrategy
     */
    public PingStrategy() {
        super(new Unpack.Builder()
                .setType(DATA, Unpack.TYPE.STRING)
                .build());
    }

    /**
     * Accept content from client and send to Server
     *
     * @param scanner Console Scanner
     * @param client  Client object
     * @throws IOException
     */
    @Override
    public void serviceUser(Console scanner, Client client) throws IOException {
        String message = scanner.askForString("Ping content:");
        long messageId = client.getMessageId();
        Pack ping = new Pack.Builder()
                .setValue("request", new OneByteInt(Client.PING_REQUEST))
                .setValue("id", messageId)
                .setValue("message", message)
                .build();
        client.send(ping);

        Unpack.Result result = keepTryingUntilReceive(client, ping, messageId);
        if (isStatusOK(result)) {
            Console.println(String.format("Received : %s", result.getString(DATA)));
        } else {
            Console.println("Failed");
        }

    }

    @Override
    public String getTitle() {
        return "Ping to server";
    }
}
