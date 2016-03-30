package com.client.strategy;

import com.Console;
import com.client.Client;
import com.client.pack.OneByteInt;
import com.client.pack.Pack;
import com.client.pack.Unpack;

import java.io.IOException;

/**
 * Created by lhtan on 23/3/16.
 * This class extend the Parent Strategy Class
 * This option enable Client to issue request
 * to write certain content into certain file
 */

public class WritingStrategy extends Strategy {

    /**
     * Class Constructor for WritingStrategy Class
     */
    public WritingStrategy() {
        super(null);
    }

    /**
     * This method serves as normal method for
     * client to read a certain file from server
     * This method does not utilize cache
     *
     * @param scanner Console Scanner
     * @param client  Client object
     * @throws IOException
     */

    @Override
    public void serviceUser(Console scanner, Client client) throws IOException {
        String filename = scanner.askForString("Writing into which file?");
        int offset = scanner.askForInteger("Starting from where");
        String content = scanner.askForString("Content");

        long messageId = client.getMessageId();
        Pack request = new Pack.Builder()
                .setValue("request", new OneByteInt(Client.WRITE_REQUEST))
                .setValue("id", messageId)
                .setValue("filename", filename)
                .setValue("offset", offset)
                .setValue("content", content)
                .build();

        client.send(request);

        Unpack.Result result = keepTryingUntilReceive(client, request, messageId);

        if (isStatusOK(result)) {
            Console.println("  WritingStrategy >> write success");
        } else {
            Console.println("  WritingStrategy >> write failed");
        }
    }

    @Override
    public String getTitle() {
        return "Write into a file without cache";
    }
}
