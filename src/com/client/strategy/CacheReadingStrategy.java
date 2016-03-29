package com.client.strategy;

import com.Console;
import com.client.Client;
import com.client.cache.Cache;
import com.client.pack.Unpack;

import java.io.IOException;

/**
 * Created by lhtan on 23/3/16.
 * This ReadingStrategy enable Client to
 * access a certain file and retrieve a
 * segment of the file
 * This class utilized Client's side caching
 */
public class CacheReadingStrategy extends Strategy {

    private final static String DATA = "data";
    private final Cache cache;
    private final CacheStrategy cacheStrategy;

    /**
     * Class Constructor of CacheReadingStrategy
     *
     * @param cache Cache object
     */
    public CacheReadingStrategy(Cache cache) {
        super(new Unpack.Builder()
                .setType(DATA, Unpack.TYPE.STRING)
                .build());
        this.cache = cache;
        this.cacheStrategy = new CacheStrategy(cache);
    }

    /**
     This method serves as normal method for client to read a certain file from server
     This method utilizes cache. The method will first try to check whether the cache contains
     the file, then only fetch the file from server if the particular file is not available in
     the client local cache
     Parameter used:
     filename    = Name of the file to be read
     offset      = offset byte from the beginning of the file
     length      = length of byte that will be read from the file
     */

    /**
     * This method serves as normal method for client to
     * read a certain file from server
     * This method utilizes cache. The method will first try to
     * check whether the cache contains the file, then only
     * fetch the file from server if the particular file is
     * not available in the client local cache
     *
     * @param scanner Console Scanner
     * @param client  Client object
     * @throws IOException
     */

    @Override
    public void serviceUser(Console scanner, Client client) throws IOException {
        String filename = scanner.askForString("Name of that porn");
        int offset = scanner.askForInteger("Episode Number");
        int length = scanner.askForInteger("Number of episodes to watch");

        Console.info("  CacheReadingStrategy >> Loading cache");
        if (this.cacheStrategy.loadCache(client, filename)) {

            String data = cache.readFromCache(filename, offset, length);

            Console.println(String.format("  CacheReadingStrategy >> Data: '%s'", data));
        } else {
            Console.println("  CacheReadingStrategy >> Server Error");
        }
    }

    @Override
    public String getTitle() {
        return "Watch Porn Hub (Cache)";
    }
}
