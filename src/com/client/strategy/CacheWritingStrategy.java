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
 */
public class CacheWritingStrategy extends Strategy {

    private final Cache cache;
    private final CacheStrategy cacheFucker;

    public CacheWritingStrategy(Cache cache) {
        super(null);
        this.cache = cache;
        this.cacheFucker = new CacheStrategy(cache);
    }


    @Override
    public void serviceUser(Console scanner, Client client) throws IOException {
        String filename = scanner.askForString("Whisper to who?");
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
            Console.println("  CacheWritingStrategy >> write success");

            //update cache as well
            if(!cache.cacheAvailable(client, filename)){
                Console.info("  CacheWritingStrategy >> FileCache is not available");
                Console.info("  CacheWritingStrategy >> Load FileCache");
                this.cacheFucker.loadCache(client, filename);
            }else {
                Console.info("  CacheWritingStrategy >> Update cache");
                cache.writeToCache(filename, offset, content);
            }
        }else{
            Console.println("  CacheWritingStrategy >> write failed");
        }
    }

    @Override
    public String getTitle() {
        return "Whisper racist contents into the ears (Cache)";
    }
}
