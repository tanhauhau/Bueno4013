package com.client.strategy;

import com.Console;
import com.client.Client;
import com.client.cache.Cache;

import java.io.IOException;

/**
 * Created by lhtan on 23/3/16.
 */
public class CacheDetailStrategy extends Strategy {

    private final Cache cache;
    public CacheDetailStrategy(Cache cache) {
        super(null);
        this.cache = cache;
    }

    /**
        This method enable client to list out all the files
        available in the cache.
     */

    @Override
    public void serviceUser(Console scanner, Client client) throws IOException {
        Console.println(" >>> Cached Listing <<< ");
        int count = 0;
        for(String filename : cache.getCachedSet()){
            System.out.println(cache.getCacheRecord(filename));
            count ++;
        }
        Console.println(String.format(" Total %d file", count));
        Console.println(" >>> Cache Listing <<< ");
    }

    @Override
    public String getTitle() {
        return "List of Cache";
    }
}
