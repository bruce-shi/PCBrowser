package com.jupiter.Service;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by lovew_000 on 2015/5/6.
 */
public class NewsPullService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private boolean scheduleRunning;
    private JobScheduler scheduler;
    private JobInfo jobInfo;
    private ComponentName jobServiceName;
    private  PullJobService mPullJobService;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(final Message msg) {

            jobServiceName = new ComponentName(getApplicationContext(), PullJobService.class);
            jobInfo = new JobInfo.Builder(1000, jobServiceName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setRequiresDeviceIdle(false)
                    .setRequiresCharging(false)
                    .setPeriodic(2000)
                    .build();
            scheduler = (JobScheduler) getApplicationContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
            int result = scheduler.schedule(jobInfo);
            if (result == JobScheduler.RESULT_SUCCESS) {
                Toast.makeText(getApplicationContext(), "job scheduler", Toast.LENGTH_SHORT).show();
            }
            /*JupiterHttpClient.get("", null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("response", response.toString());
                    stopSelf();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    stopSelf();
                }
            });*/
        }
    }
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        //HandlerThread thread = new HandlerThread("ServiceStartArguments",
        //        Process.THREAD_PRIORITY_BACKGROUND);
        //thread.start();
        Toast.makeText(this, "service create", Toast.LENGTH_SHORT).show();
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = this.getMainLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        scheduleRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service start", Toast.LENGTH_SHORT).show();
        if(!scheduleRunning) {
            Toast.makeText(this, "start schedule", Toast.LENGTH_SHORT).show();
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
        scheduler.cancelAll();
        Log.d("Service","Service Stop");
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

}
