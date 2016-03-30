package com.client.strategy;

import com.Console;
import com.client.Client;
import com.client.cache.Cache;
import com.client.pack.OneByteInt;
import com.client.pack.Pack;
import com.client.pack.Unpack;

import java.io.IOException;

/**
 * Created by lhtan on 23/3/16.
 * This class extend the Parent Strategy Class
 * This option enable Client create a
 * new file in the server
 */

public class CreateFileStrategy extends Strategy {

    private final Cache cache;
    /**
     * Class Constructor for CreateFileStrategy Class
     */
    public CreateFileStrategy(Cache cache) {
        super(null);
        this.cache = cache;
    }

    /**
     * This method serves as normal method for
     * client to create a new file in the server
     * This method does not utilize cache
     *
     * @param scanner Console Scanner
     * @param client  Client object
     * @throws IOException
     */

    @Override
    public void serviceUser(Console scanner, Client client) throws IOException {
        String filename = scanner.askForString("Name of the file:");

        long messageId = client.getMessageId();
        Pack request = new Pack.Builder()
                .setValue("request", new OneByteInt(Client.NEW_FILE_REQUEST))
                .setValue("id", messageId)
                .setValue("filename", filename)
                .build();

        client.send(request);

        Unpack.Result result = keepTryingUntilReceive(client, request, messageId);

        if (isStatusOK(result)) {
            Console.println("  CreateFileStrategy >> create success");
            cache.writeToCache(filename, "");
        } else {
            Console.println("  CreateFileStrategy >> create failed");
        }
    }

    @Override
    public String getTitle() {
        return "Create a new file";
    }
}
