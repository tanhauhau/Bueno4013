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
 * This class is similar with the WritingStrategy Class,
 * but this class utilize Client's side caching
 */
public class CacheWritingStrategy extends Strategy {

    private final Cache cache;
    private final CacheStrategy cacheStrategy;

    /**
     * Class Constructor for CacheWritingStrategy Class
     *
     * @param cache Cache object
     */
    public CacheWritingStrategy(Cache cache) {
        super(null);
        this.cache = cache;
        this.cacheStrategy = new CacheStrategy(cache);
    }

    /**
     * This method serves as normal method for client to
     * write content into a certain file from server
     * If the file is available in cache, the file in cache
     * will be updated, then it will be send back to server
     * for cache content update
     *
     * @param scanner Console Scanner
     * @param client  Client object
     * @throws IOException
     */
    @Override
    public void serviceUser(Console scanner, Client client) throws IOException {
        String filename = scanner.askForString("Name of the file:");
        int offset = scanner.askForInteger("Offset:");
        String content = scanner.askForString("Data:");

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
            Console.println("  CacheWritingStrategy >> write success");

            //update cache as well
            if (!cache.cacheAvailable(client, filename)) {
                Console.info("  CacheWritingStrategy >> FileCache is not available");
                Console.info("  CacheWritingStrategy >> Load FileCache");
                this.cacheStrategy.loadCache(client, filename);
            } else {
                Console.info("  CacheWritingStrategy >> Update cache");
                cache.writeToCache(filename, offset, content);
            }
        } else {
            Console.println("  CacheWritingStrategy >> write failed");
        }
    }

    @Override
    public String getTitle() {
        return "Write data into the file (cache)";
    }
}
