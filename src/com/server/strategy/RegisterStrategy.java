package com.server.strategy;

import com.server.callback.Callback;
import com.server.pack.Unpack;

import java.io.File;
import java.io.IOException;

/**
 * Created by lhtan on 23/3/16.
 *
 * Register client into callback and update the client whenever the
 * content of the particular file is changed and updated, until the client
 * reached its timeout
 */

public class RegisterStrategy extends Strategy {

    private final static String FILENAME = "filename";
    private final static String MONITOR_INTERVAL = "interval";
    private final String folder;
    private final Callback callback;

    /**
     * Class Constructor for RegisterStrategy
     *
     * @param folder
     * @param callback
     */
    public RegisterStrategy(String folder, Callback callback) {
        super(new Unpack.Builder()
                .setType(FILENAME, Unpack.TYPE.STRING)
                .setType(MONITOR_INTERVAL, Unpack.TYPE.INTEGER)
                .build());
        this.callback = callback;
        this.folder = folder;
    }

    /**
     * Handle method inherited from Parent Strategy Class
     * This method will register client who requested into callback
     * and also update clients if there is any changes on the certain file
     *
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    protected byte[] handle(Request request) throws IOException {
        Unpack.Result result = request.getData();
        String filename = result.getString(FILENAME);
        Integer interval = result.getInt(MONITOR_INTERVAL);

        if (ifAnyNull(filename, interval))
            return replyError(request.getRequestID(), "Corrupted data");    /* Return error message if the filename or interval is NULL */

        File file = new File(folder, filename);
        if (!file.exists()){
            return replyError(request.getRequestID(), "No such file");      /* Return error message if the file asked doesn't exist */
        }else{
            callback.register(filename, interval, request.getRequestID(), request.getAddress(), request.getPort());     /* Return Success message after callback is registered */
            System.out.println("  RegisterStrategy >> Registered to monitor");
            return replySuccess(request.getRequestID(), "Success");
        }
    }
}
