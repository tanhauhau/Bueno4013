package com.server.strategy;

import com.server.pack.Unpack;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by lhtan on 22/3/16.
 * This ReadingStrategy class accept client's request
 * on reading a particular file
 */

public class ReadingStrategy extends Strategy {

    private String folder = "";
    private final static String FILENAME = "filename";
    private final static String OFFSET = "offset";
    private final static String LENGTH = "length";

    /**
     * Class Constructor of ReadingStrategy
     * @param folder
     */
    public ReadingStrategy(String folder) {
        super(new Unpack.Builder()
                    .setType(FILENAME, Unpack.TYPE.STRING)
                    .setType(OFFSET, Unpack.TYPE.LONG)
                    .setType(LENGTH, Unpack.TYPE.LONG)
                    .build());
        this.folder = folder;
    }

    /**
     * Handle method inherited from Parent Strategy Class
     * Accept request from client and return the content
     * client wish to read, otherwise error message will
     * be returned
     *
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    protected byte[] handle(Request request) throws IOException {
        Unpack.Result result = request.getData();
        String filename = result.getString(FILENAME);
        Long offset = result.getLong(OFFSET);
        Long length = result.getLong(LENGTH);

        if (ifAnyNull(filename, offset, length))
            return replyError(request.getRequestID(), "Corrupted data");        /* Return error message if any of the parameters is NULL */

        File file = new File(folder, filename);
        if (!file.exists()){
            return replyError(request.getRequestID(), "No such file");          /* Return error messages if the file asked doesn't exist */
        }else{
            long fileByteSize = file.length();                                  /* Acquire size of the file */
            if (offset >= fileByteSize){
                return replyError(request.getRequestID(), "Offset too big!!!"); /* Return error message if offset exceed file size */
            }else if (offset + length > fileByteSize){
                return replyError(request.getRequestID(), "Length too big!!!"); /* Return error message if length with offset exceed file size */
            }else {
                System.out.println(String.format("  Reading Strategy >> Read %s with offset=%d length=%d", filename, offset, length));
                byte[] buffer = new byte[length.intValue()];
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.seek(offset);                                               /* Access file from offset */
                raf.read(buffer, 0, length.intValue());                         /* Read the file for the length */
                System.out.println(String.format("  Reading Strategy >> Read '%s'", new String(buffer)));   /* Return the segment from reading the file with success message */
                return replySuccess(request.getRequestID(), buffer);
            }
        }
    }
}
