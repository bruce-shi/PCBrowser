package com.jupiter.Service;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.os.Process;
import android.util.Log;
import com.jupiter.pcbrowser.*;



/**
 * Created by lovew_000 on 2015/5/6.
 */
public class NewsPullService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private JobScheduler scheduler;
    private JobInfo jobInfo;
    private ComponentName jobServiceName;

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper,Context context) {
            super(looper);

        }
        @Override
        public void handleMessage(final Message msg) {
            scheduleJob();
        }
    }

    public NewsPullService(){
        //android.os.Debug.waitForDebugger();
    }
    private void scheduleJob(){
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sharedpref), Context.MODE_MULTI_PROCESS);

        boolean auto_push = sharedPref.getBoolean(SettingsActivity.AUTO_PUSH_KEY, true);
        if(auto_push) {
            boolean only_charge = sharedPref.getBoolean(SettingsActivity.ONLY_CHARGE_KEY, false);
            boolean wifi_only = sharedPref.getBoolean(SettingsActivity.ONLY_WIFI_KEY, false);
            long period = 1;
            try {
                period = Long.valueOf(sharedPref.getString(SettingsActivity.UPDATE_PERIOD_KEY, "10"));
                jobServiceName = new ComponentName(this, PullJobService.class);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            jobInfo = new JobInfo.Builder(1000, jobServiceName)
                    .setRequiredNetworkType(wifi_only ? JobInfo.NETWORK_TYPE_UNMETERED : JobInfo.NETWORK_TYPE_ANY)
                    .setRequiresDeviceIdle(false)
                    .setRequiresCharging(only_charge)
                    .setPeriodic(period * 1000)//period * 60000)
                    .build();
            try {
                scheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                int result = scheduler.schedule(jobInfo);
                if (result == JobScheduler.RESULT_SUCCESS) {
                    Log.d("Server","scheduled");
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }
    }

    public void onCreate() {

        mServiceLooper = this.getMainLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper,this);



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(scheduler == null) {

            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            mServiceHandler.sendMessage(msg);
        }

        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        if(scheduler != null) {
            scheduler.cancelAll();
            scheduler = null ;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private MStub mBinder = new MStub();

    private final class MStub extends ILocalService.Stub{
        public MStub(){
        }

        public int getPid() {
            //return 0;
            return Process.myPid();
        }

        public void restartServer() throws RemoteException {
            //android.os.Debug.waitForDebugger();
            if(scheduler != null) {
                scheduler.cancelAll();
                scheduler = null;
            }

            scheduleJob();
            Log.d("Server","restart");
        }

        public void startServer() throws RemoteException{
            if(scheduler == null) {
                scheduleJob();
                Log.d("Server", "start");
            }else{
                Log.d("Server","already start");
            }
        }
    }
}
