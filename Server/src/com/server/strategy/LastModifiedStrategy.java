package com.server.strategy;

import com.server.pack.Pack;
import com.server.pack.Unpack;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by lhtan on 23/3/16.
 * LastModifiedStrategy class return the timestamp when
 * the asked file was last modified
 */

public class LastModifiedStrategy extends Strategy {

    private String folder = "";
    private final static String FILENAME = "filename";

    /**
     * Class Constructor for LastModifiedStrategy class
     * @param folder        Folder containing files
     */
    public LastModifiedStrategy(String folder) {
        super(new Unpack.Builder()
                .setType(FILENAME, Unpack.TYPE.STRING)
                .build());
        this.folder = folder;
    }

    /**
     * Handle method inherited by the Parent Strategy Class
     * Return the timestamp of the requested file last modified
     *
     * @param request       Request from Client
     * @return
     * @throws IOException
     */

    @Override
    protected byte[] handle(Request request) throws IOException {
        Unpack.Result result = request.getData();
        String filename = result.getString(FILENAME);

        if (ifAnyNull(filename))
            return replyError(request.getRequestID(), "Corrupted data");        /* Return error message if the parameter is NULL */

        File file = new File(folder, filename);
        if (!file.exists()){
            return replyError(request.getRequestID(), "No such file");          /* Return error message if the file doesn't exist */
        }else {
            Long lastModified = file.lastModified();                            /* Return the timestamp with success message */
            System.out.println(String.format("  LastModifiedStrategy >> file %s last modified = %s", filename, new Date(lastModified).toString()));
            return replySuccess(request.getRequestID(), new Pack.Builder()
                    .setValue("time", lastModified)
                    .build());
        }
    }
}
