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
 * This Class acts as a Client's side caching
 */
public class CacheStrategy extends Strategy {

    private final static String DATA = "data";
    private final Cache cache;
    private final SizeStrategy sizeStrategy;

    public CacheStrategy(Cache cache) {
        super(new Unpack.Builder()
                .setType(DATA, Unpack.TYPE.STRING)
                .build());
        this.cache = cache;
        this.sizeStrategy = new SizeStrategy();
    }

    /**
     * This method will load the file available in cache to client
     *
     * @param client   Client object
     * @param filename Name of the file where client wants to access
     * @return boolean true (if available) or false
     * @throws IOException
     */

    public boolean loadCache(Client client, String filename) throws IOException {
        if (cache.cacheAvailable(client, filename)) {
            System.out.println("  CacheStrategy >> FileCache is available, do nothing");
            return true;
        } else {
            System.out.println("  CacheStrategy >> Load FileCache");
            long fileSizeInByte = this.sizeStrategy.getFileSize(client, filename);

            long messageId = client.getMessageId();

            Pack request = new Pack.Builder()
                    .setValue("request", new OneByteInt(Client.READ_REQUEST))
                    .setValue("id", messageId)
                    .setValue("filename", filename)
                    .setValue("offset", 0L)
                    .setValue("length", fileSizeInByte)
                    .build();

            client.send(request);

            Unpack.Result result = keepTryingUntilReceive(client, request, messageId);

            if (isStatusOK(result)) {
                String data = result.getString(DATA);

                Console.println("  CacheStrategy >> Update FileCache");
                cache.writeToCache(filename, data);
                return true;
            } else {
                Console.println(String.format("  CacheStrategy >> Error: %s", result.getString(DATA)));
                return false;
            }
        }
    }

    /**
     * Accept File name input from client and load the file from cache
     *
     * @param scanner Console Scanner
     * @param client  Client object
     * @throws IOException
     */
    @Override
    public void serviceUser(Console scanner, Client client) throws IOException {
        String filename = scanner.askForString("Name of the file:");
        loadCache(client, filename);
    }

    @Override
    public String getTitle() {
        return "Cache the file";
    }
}
