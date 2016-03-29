package com.client.strategy;

import com.Console;
import com.client.Client;
import com.client.cache.Cache;

import java.io.IOException;

/**
 * Created by lhtan on 23/3/16.
 * This Class will list out all the content
 * available in the cache
 */
public class CacheDetailStrategy extends Strategy {

    private final Cache cache;

    /**
     * Class Constructor of CacheDetailStrategy
     *
     * @param cache Cache object
     */
    public CacheDetailStrategy(Cache cache) {
        super(null);
        this.cache = cache;
    }

    /**
     * This method will list out all the files available
     * in the Client's cache
     *
     * @param scanner Console Scanner
     * @param client  Client object
     * @throws IOException
     */
    @Override
    public void serviceUser(Console scanner, Client client) throws IOException {
        Console.println(" >>> Cached Listing <<< ");
        int count = 0;
        for (String filename : cache.getCachedSet()) {
            System.out.println(cache.getCacheRecord(filename));
            count++;
        }
        Console.println(String.format(" Total %d file", count));
        Console.println(" >>> Cache Listing <<< ");
    }

    @Override
    public String getTitle() {
        return "List of Cache";
    }
}
