package com.server.strategy;

import com.server.callback.Callback;
import com.server.pack.Unpack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by lhtan on 23/3/16.
 */
/*
    Duplicated the entire content of the file
    This is an example of non-idempotent request
 */
public class DoubleStrategy extends Strategy {

    private final static String FILENAME = "filename";
    private final String folder;
    private Callback callback;
    public DoubleStrategy(String folder, Callback callback) {
        super(new Unpack.Builder()
                .setType(FILENAME, Unpack.TYPE.STRING)
                .build());
        this.folder = folder;
        this.callback = callback;
    }

    @Override
    protected byte[] handle(Request request) throws IOException {
        Unpack.Result result = request.getData();
        String filename = result.getString(FILENAME);

        if (ifAnyNull(filename)) return replyError(request.getRequestID(), "Corrupted data");

        File file = new File(folder, filename);
        if (!file.exists()){
            return replyError(request.getRequestID(), "No such file");
        }else{
            Path path = Paths.get(folder, filename);
            byte[] data = Files.readAllBytes(path);
            byte[] duplicate = new byte[data.length * 2];
            System.arraycopy(data, 0, duplicate, 0, data.length);
            System.arraycopy(data, 0, duplicate, data.length, data.length);
            Files.write(path, duplicate, StandardOpenOption.WRITE);
            //inform
            callback.inform(filename, request.getSocket());

            return replySuccess(request.getRequestID(), "Non Idempotent");
        }
    }
}
