package com.server.strategy;

import com.server.pack.Pack;
import com.server.pack.Unpack;

import java.io.File;
import java.io.IOException;

/**
 * Created by lhtan on 23/3/16.
 * This SizeStrategy class handles the Client's request on checking the
 * size of the file.
 * This class extend to the Parent Strategy class
 */

public class SizeStrategy extends Strategy {

    private String folder = "";
    private final static String FILENAME = "filename";

    /**
     * Class Constructer of SizeStrategy
     * @param folder    folder containing files
     */

    public SizeStrategy(String folder) {
        super(new Unpack.Builder()
                .setType(FILENAME, Unpack.TYPE.STRING)
                .build());
        this.folder = folder;
    }

    /**
     * Handle method inherited from Parent Class
     * This method will return the size of the file asked
     * otherwise error message is returned
     *
     * @param request   Request object from client
     * @return
     * @throws IOException
     */
    @Override
    protected byte[] handle(Request request) throws IOException {
        Unpack.Result result = request.getData();
        String filename = result.getString(FILENAME);

        if (ifAnyNull(filename))
            return replyError(request.getRequestID(), "Corrupted data");    /* Return error message if the filename parameter is NULL */

        File file = new File(folder, filename);
        if (!file.exists()){
            return replyError(request.getRequestID(), "No such file");      /* Return error message if file doesn't exist */
        }else {
            Long fileByteSize = file.length();                              /* Acquire file size and return with success message */
            System.out.println(String.format("  SizeStrategy >> file %s size = %d bytes", filename, fileByteSize));
            return replySuccess(request.getRequestID(), new Pack.Builder()
                                    .setValue("size", fileByteSize)
                                    .build());
        }
    }
}
