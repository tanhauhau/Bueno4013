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
public class ReadingStrategy extends Strategy {

    private final static String DATA = "data";

    public ReadingStrategy() {
        super(new Unpack.Builder()
                .setType(DATA, Unpack.TYPE.STRING)
                .build());
    }

    /**
        This method serves as normal method for client to read a certain file from server
        This method does not utilize cache
        Parameter used:
        filename    = Name of the file to be read
        offset      = offset byte from the beginning of the file
        length      = length of byte that will be read from the file
     */

    @Override
    public void serviceUser(Console scanner, Client client) throws IOException {
        String filename = scanner.askForString("Name of that porn");
        int offset = scanner.askForInteger("Episode Number");
        int length = scanner.askForInteger("Number of episodes to watch");

        long messageId = client.getMessageId();

        Pack request = new Pack.Builder()
                .setValue("request", new OneByteInt(Client.READ_REQUEST))
                .setValue("id", messageId)
                .setValue("filename", filename)
                .setValue("offset", (long) offset)
                .setValue("length", (long) length)
                .build();

        client.send(request);

        Unpack.Result result = keepTryingUntilReceive(client, request, messageId);

        if (isStatusOK(result)) {
            Console.println(String.format("  ReadingStrategy >> Data: '%s'", result.getString(DATA)));
        }else{
            Console.println(String.format("  ReadingStrategy >> Error: %s", result.getString(DATA)));
        }
    }

    @Override
    public String getTitle() {
        return "Read a file from server without cache";
    }
}
