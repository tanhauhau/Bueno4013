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
 * DoubleStrategy Class handle client's request to double the
 * file size by duplicating the entire content of the file
 * This is an example of non-idempotent request
 */
public class DoubleStrategy extends Strategy {

    private final static String FILENAME = "filename";
    private final String folder;
    private Callback callback;

    /**
     * Class Constructor for DoubleStrategy
     * @param folder        Folder containing files
     * @param callback      List of callback that contain registered clients
     */
    public DoubleStrategy(String folder, Callback callback) {
        super(new Unpack.Builder()
                .setType(FILENAME, Unpack.TYPE.STRING)
                .build());
        this.folder = folder;
        this.callback = callback;
    }

    /**
     * Handle method inherited from Parent Strategy Class
     * The handler will duplicate the entire content of the file once,
     * hence resulting in doubling the filesize of the file
     * @param request       Request from Client
     * @return
     * @throws IOException
     */
    @Override
    protected byte[] handle(Request request) throws IOException {
        Unpack.Result result = request.getData();
        String filename = result.getString(FILENAME);

        if (ifAnyNull(filename))
            return replyError(request.getRequestID(), "Corrupted data");        /* Return error message if the filename is NULL */

        File file = new File(folder, filename);
        if (!file.exists()){
            return replyError(request.getRequestID(), "No such file");          /* Return error message if the file doesn't exist */
        }else{
            Path path = Paths.get(folder, filename);
            byte[] data = Files.readAllBytes(path);                             /* Read the entire content of the file */
            byte[] duplicate = new byte[data.length * 2];
            System.arraycopy(data, 0, duplicate, 0, data.length);               /* Duplicate the content */
            System.arraycopy(data, 0, duplicate, data.length, data.length);
            Files.write(path, duplicate, StandardOpenOption.WRITE);
            callback.inform(filename, request.getSocket());                     /* Inform registered client regarding the change of the file */

            return replySuccess(request.getRequestID(), "Non Idempotent");      /* Return success message */
        }
    }
}
