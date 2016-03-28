package com.server.strategy;

import com.server.callback.Callback;
import com.server.pack.Unpack;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by lhtan on 22/3/16.
 *
 * This class extend the Parent Class Strategy
 * This class is incharge of handling writing request from client
 */


public class WritingStrategy extends Strategy {
    private String folder = "";
    private Callback callback;

    private final static String FILENAME = "filename";
    private final static String OFFSET = "offset";
    private final static String DATA = "data";

    /**
     * Class Constructor for WritingStrategy
     *
     * @param folder
     * @param callback
     */

    public WritingStrategy(String folder, Callback callback) {
        super(new Unpack.Builder()
                .setType(FILENAME, Unpack.TYPE.STRING)
                .setType(OFFSET, Unpack.TYPE.INTEGER)
                .setType(DATA, Unpack.TYPE.STRING)
                .build());
        this.folder = folder;
        this.callback = callback;
    }

    /**
     * handle method inherited from Parent Class Strategy and
     * mandatory to implement
     *
     * Find the file (if available) and write the content into it
     * otherwise error message is returned
     *
     * @param request
     * @return result message
     * @throws IOException
     */
    @Override
    protected byte[] handle(Request request) throws IOException {
        Unpack.Result result = request.getData();
        String filename = result.getString(FILENAME);
        Integer offset = result.getInt(OFFSET);
        String data = result.getString(DATA);

        if (ifAnyNull(filename, offset, data))
            return replyError(request.getRequestID(), "Corrupted data");    /* Check is there any null object in parameter */

        File file = new File(folder, filename);
        if (!file.exists()){
            return replyError(request.getRequestID(), "No such file");      /* Return error message if file not found */
        }else {
            long fileByteSize = file.length();
            if (offset > fileByteSize) {
                return replyError(request.getRequestID(), "Offset too big!!! ");    /* Return error message if offset exceed the size of the file */
            } else {
                System.out.println(String.format("  Writing Strategy >> Write %s with offset=%d data=%s", filename, offset, data));
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                raf.seek(offset);           /* move the pointer of the file to the offset location */
                raf.writeBytes(data);       /* write the content into the file */
                raf.close();                /* close file */
                callback.inform(filename, request.getSocket());     /* inform client who registered for callback about the changes of file */

                return replySuccess(request.getRequestID(), "Write Successful");    /* Return success message */
            }
        }
    }


}
