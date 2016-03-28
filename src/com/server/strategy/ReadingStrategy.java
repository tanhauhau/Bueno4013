package com.server.strategy;

import com.server.pack.Unpack;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by lhtan on 22/3/16.
 */

/*
    Service Handler by retrieving the content the client
    want to read and send it back to client
 */
public class ReadingStrategy extends Strategy {

    private String folder = "";
    private final static String FILENAME = "filename";
    private final static String OFFSET = "offset";
    private final static String LENGTH = "length";

    public ReadingStrategy(String folder) {
        super(new Unpack.Builder()
                    .setType(FILENAME, Unpack.TYPE.STRING)
                    .setType(OFFSET, Unpack.TYPE.LONG)
                    .setType(LENGTH, Unpack.TYPE.LONG)
                    .build());
        this.folder = folder;
    }

    @Override
    protected byte[] handle(Request request) throws IOException {
        Unpack.Result result = request.getData();
        String filename = result.getString(FILENAME);
        Long offset = result.getLong(OFFSET);
        Long length = result.getLong(LENGTH);

        if (ifAnyNull(filename, offset, length))   return replyError(request.getRequestID(), "Corrupted data");

        File file = new File(folder, filename);
        if (!file.exists()){
            return replyError(request.getRequestID(), "No such file");
        }else{
            long fileByteSize = file.length();
            if (offset >= fileByteSize){
                return replyError(request.getRequestID(), "Offset too big!!!");
            }else if (offset + length > fileByteSize){
                return replyError(request.getRequestID(), "Length too big!!!");
            }else {
                System.out.println(String.format("  Reading Strategy >> Read %s with offset=%d length=%d", filename, offset, length));
                byte[] buffer = new byte[length.intValue()];
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.seek(offset);
                raf.read(buffer, 0, length.intValue());
                System.out.println(String.format("  Reading Strategy >> Read '%s'", new String(buffer)));
                return replySuccess(request.getRequestID(), buffer);
            }
        }
    }
}
