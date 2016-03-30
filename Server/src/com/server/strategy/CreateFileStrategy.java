package com.server.strategy;

import com.server.pack.Unpack;

import java.io.File;
import java.io.IOException;

/**
 * Created by lhtan on 22/3/16.
 *
 * This class extend the Parent Class Strategy
 * This class is incharge of handling writing request from client
 */


public class CreateFileStrategy extends Strategy {
    private String folder = "";

    private final static String FILENAME = "filename";
    private final static String OFFSET = "offset";
    private final static String DATA = "data";

    /**
     * Class Constructor for CreateFileStrategy
     *
     * @param folder folder containing the files
     */

    public CreateFileStrategy(String folder) {
        super(new Unpack.Builder()
                .setType(FILENAME, Unpack.TYPE.STRING)
                .build());
        this.folder = folder;
    }

    /**
     * handle method inherited from Parent Class Strategy and
     * mandatory to implement
     *
     * Find the file (if available) and write the content into it
     * otherwise error message is returned
     *
     * @param request Request object from client
     * @return result message
     * @throws IOException
     */
    @Override
    protected byte[] handle(Request request) throws IOException {
        Unpack.Result result = request.getData();
        String filename = result.getString(FILENAME);

        if (ifAnyNull(filename))
            return replyError(request.getRequestID(), "Corrupted data");    /* Check is there any null object in parameter */

        File file = new File(folder, filename);
        if (file.exists() || file.createNewFile()){
            return replySuccess(request.getRequestID(), "Successful");    /* Return success message */
        }else {
            return replyError(request.getRequestID(), "Create failed");      /* Return error message if file not found */
        }
    }
}
