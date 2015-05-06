package com.jupiter.Service;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONObject;


/**
 * Created by lovew_000 on 2015/5/6.
 */
public class NewsPullService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private boolean scheduleRunning;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(final Message msg) {
            JupiterHttpClient.get("", null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("response",response.toString());
                    //stopSelf();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    //stopSelf();
                }
            });
        }
    }
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        /*HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();*/

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = this.getMainLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        scheduleRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "Serviec Start");
        if(!scheduleRunning) {

            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            mServiceHandler.sendMessage(msg);
            scheduleRunning = true;
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("Service","Service Stop");
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

}
