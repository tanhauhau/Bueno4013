package com.server.strategy;

import com.server.pack.Pack;
import com.server.pack.Unpack;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by lhtan on 23/3/16.
 */
/*
    Retrieve the last validated time of a certain file
 */
public class LastModifiedStrategy extends Strategy {

    private String folder = "";
    private final static String FILENAME = "filename";

    public LastModifiedStrategy(String folder) {
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
            Long lastModified = file.lastModified();
            System.out.println(String.format("  LastModifiedStrategy >> file %s last modified = %s", filename, new Date(lastModified).toString()));
            return replySuccess(request.getRequestID(), new Pack.Builder()
                    .setValue("time", lastModified)
                    .build());
        }
    }
}
