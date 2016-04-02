package com.client.cache;

import com.client.Client;
import com.client.strategy.LastModifiedStrategy;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by lhtan on 23/3/16.
 */

/**
 * This class serves as an abstract class for the cache daughter class
 */
public abstract class Cache {
    private final int freshness;
    private final LastModifiedStrategy lastModifiedStrategy;

    final HashMap<String, Record> cacheTable;

    /**
     * @param freshness number of second
     */


    public Cache(int freshness) {
        this.freshness = freshness;
        this.lastModifiedStrategy = new LastModifiedStrategy();
        this.cacheTable = new HashMap<>();
    }

    /*
        This method check the validity of the file in cache
        If the file is unavailable in cache, a cache miss occured and false will be return
        If the file in available in cache, then the freshness of the file will be validated
        if the freshness of the file is still within the freshness interval, a cache hit occured,
        the freshness of the file is updated
        If the freshness exceed the freshness interval, a false will be returned and the latest
        file will be fetched form server and the freshness will be updated
     */
    public boolean cacheAvailable(Client client, String filename) throws IOException {
        if (!this.cacheTable.containsKey(filename)) {
            System.out.println(String.format("   FileCache >> cache miss for file %s", filename));
            //1. cache miss
            return false;
        } else {
            //2. cache hit
            Record cacheRecord = this.cacheTable.get(filename);
            if (cacheRecord.isFresh()) {
                //2.1. cache is fresh
                return true;
            } else {
                //2.2. cache is sour
                long lastModified = this.lastModifiedStrategy.lastUpdate(client, filename);
                if (cacheRecord.isInvalid(lastModified)) {
                    System.out.println(String.format("   FileCache >> cache record is invalid, cache version: %s, server last modified: %s", new Date(cacheRecord.lastValidated).toString(), new Date(lastModified).toString()));
                    return false;
                } else {
                    //2.2.2. update tc
                    cacheRecord.setLastValidated(System.currentTimeMillis());
                    return true;
                }
            }
        }
    }

    /*
        Update the Cache depends on the availability of the file
     */
    public void updateCacheRecord(String filename) {
        long lastValidate = System.currentTimeMillis();
        if (!cacheTable.containsKey(filename)) {
            cacheTable.put(filename, createRecord(filename, lastValidate));
        } else {
            cacheTable.get(filename).setLastValidated(lastValidate);
        }
    }

    protected abstract Record createRecord(String filename, long lastValidate);

    public abstract String readFromCache(String filename, int offset, int length) throws IOException;

    public abstract void writeToCache(String filename, int offset, String content) throws IOException;

    public abstract void writeToCache(String filename, String content) throws IOException;

    public Set<String> getCachedSet() {
        return cacheTable.keySet();
    }

    public Record getCacheRecord(String filename) {
        if (cacheTable.containsKey(filename)) {
            return cacheTable.get(filename);
        }
        return null;
    }

    protected class Record {
        protected final String filename;
        protected long lastValidated;

        public Record(String filename, long lastValidated) {
            this.filename = filename;
            this.lastValidated = lastValidated;
        }

        public void setLastValidated(long time) {
            lastValidated = time;
        }

        public boolean isInvalid(long time) {
            return lastValidated < time;
        }

        public boolean isFresh() {
            return System.currentTimeMillis() - lastValidated < freshness * 1000;
        }

        @Override
        public String toString() {
            return String.format("%s, tc=%s, %s", filename, new Date(lastValidated).toString(), isFresh() ? "fresh" : "sour");
        }
    }
}
