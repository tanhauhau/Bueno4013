package com.server.strategy;

import com.server.pack.Pack;
import com.server.pack.Unpack;

import java.io.File;
import java.io.IOException;

/**
 * Created by lhtan on 23/3/16.
 */

/*
    Check the file size and return the size to the client
 */
public class SizeStrategy extends Strategy {

    private String folder = "";
    private final static String FILENAME = "filename";

    public SizeStrategy(String folder) {
        super(new Unpack.Builder()
                .setType(FILENAME, Unpack.TYPE.STRING)
                .build());
        this.folder = folder;
    }

    @Override
    protected byte[] handle(Request request) throws IOException {
        Unpack.Result result = request.getData();
        String filename = result.getString(FILENAME);

        if (ifAnyNull(filename))   return replyError(request.getRequestID(), "Corrupted data");

        File file = new File(folder, filename);
        if (!file.exists()){
            return replyError(request.getRequestID(), "No such file");
        }else {
            Long fileByteSize = file.length();
            System.out.println(String.format("  SizeStrategy >> file %s size = %d bytes", filename, fileByteSize));
            return replySuccess(request.getRequestID(), new Pack.Builder()
                                    .setValue("size", fileByteSize)
                                    .build());
        }
    }
}
