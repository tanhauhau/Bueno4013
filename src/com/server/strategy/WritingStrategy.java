package com.server.strategy;

import com.server.callback.Callback;
import com.server.pack.Unpack;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by lhtan on 22/3/16.
 */
/*
    Receive the content that the client want to write into a
    certain file and reply the result to the client
 */

public class WritingStrategy extends Strategy {
    private String folder = "";
    private Callback callback;

    private final static String FILENAME = "filename";
    private final static String OFFSET = "offset";
    private final static String DATA = "data";

    public WritingStrategy(String folder, Callback callback) {
        super(new Unpack.Builder()
                .setType(FILENAME, Unpack.TYPE.STRING)
                .setType(OFFSET, Unpack.TYPE.INTEGER)
                .setType(DATA, Unpack.TYPE.STRING)
                .build());
        this.folder = folder;
        this.callback = callback;
    }

    @Override
    protected byte[] handle(Request request) throws IOException {
        Unpack.Result result = request.getData();
        String filename = result.getString(FILENAME);
        Integer offset = result.getInt(OFFSET);
        String data = result.getString(DATA);

        if (ifAnyNull(filename, offset, data))   return replyError(request.getRequestID(), "Corrupted data");

        File file = new File(folder, filename);
        if (!file.exists()){
            return replyError(request.getRequestID(), "No such file");
        }else {
            long fileByteSize = file.length();
            if (offset > fileByteSize) {
                return replyError(request.getRequestID(), "Offset too big!!! ");
            } else {
                System.out.println(String.format("  Writing Strategy >> Write %s with offset=%d data=%s", filename, offset, data));
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                raf.seek(offset);
                raf.writeBytes(data);
                raf.close();
                //inform
                callback.inform(filename, request.getSocket());

                return replySuccess(request.getRequestID(), "Write Successful");
            }
        }
    }


}
