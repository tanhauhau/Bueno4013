package com.client.cache;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

/**
 * Created by lhtan on 23/3/16.
 */
public class MemoryCache extends Cache {
    private final String folder;
    /**
     * @param folder cache folder path
     * @param freshness number of second
     */
    public MemoryCache(String folder, int freshness) {
        super(freshness);
        this.folder = folder;
    }

    @Override
    protected Record createRecord(String filename, long lastValidate) {
        Path path = Paths.get(folder, filename);
        return new FileRecord(filename, path.toString(), lastValidate);
    }

    public String readFromCache(String filename, int offset, int length) throws IOException{
        byte[] buffer = new byte[length];
        RandomAccessFile raf = new RandomAccessFile(getCacheFilePath(filename), "r");
        raf.seek(offset);
        raf.read(buffer, 0, length);
        return new String(buffer);
    }

    public void writeToCache(String filename, int offset, String content) throws IOException{
        RandomAccessFile raf = new RandomAccessFile(getCacheFilePath(filename), "rw");
        raf.seek(offset);
        raf.writeBytes(content);
        raf.close();
        updateCacheRecord(filename);
    }
    public void writeToCache(String filename, String content) throws IOException{
        String path = getCacheFilePath(filename);
        Path filepath = Paths.get(path);
        if (!Files.exists(filepath)) Files.createFile(filepath);
        Files.write(filepath, content.getBytes(), StandardOpenOption.WRITE);
        updateCacheRecord(filename);
    }

    public String getCacheFilePath(String filename){
        if (cacheTable.containsKey(filename)){
            return ((FileRecord) cacheTable.get(filename)).cacheFilepath;
        }
        return null;
    }

    private class FileRecord extends Record {
        private String cacheFilepath;

        public FileRecord(String filename, String cacheFilepath, long lastValidated) {
            super(filename, lastValidated);
            this.cacheFilepath = cacheFilepath;
        }
        @Override
        public String toString() {
            return String.format("%s, tc=%s, %s", filename, new Date(lastValidated).toString(), isFresh()?"fresh":"sour");
        }
    }
}
