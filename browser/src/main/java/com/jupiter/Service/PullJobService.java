package com.jupiter.Service;

import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.jupiter.model.CachedNews;
import com.jupiter.pcbrowser.MainActivity;
import com.jupiter.pcbrowser.PCConstant;
import com.jupiter.pcbrowser.R;
import com.jupiter.pcbrowser.SettingsActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by lovew_000 on 2015/5/7.
 */
public class PullJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {

        SharedPreferences sp = getApplication().getSharedPreferences(getString(R.string.sharedpref), Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        boolean only_charge = sp.getBoolean(SettingsActivity.ONLY_CHARGE_KEY, false);
        boolean wifi_only = sp.getBoolean(SettingsActivity.ONLY_WIFI_KEY, false);
        String server_url = sp.getString(SettingsActivity.SERVER_URL,"mobile_news");

        //get network status
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        //get battery status
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;



        if(wifi_only && !isWiFi){
            return true;
        }
        if(only_charge && !isCharging){
            return true;
        }

        TelephonyManager tManager;
        tManager = (TelephonyManager) getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        String uuid = tManager.getDeviceId();




        Bundle extra = new Bundle();
        extra.putString("app_key",sp.getString(getString(R.string.appkey), ""));
        extra.putString("uuid",uuid);
        extra.putString("url",server_url);
        new JobTask(this,getApplicationContext(),extra).execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return false;
    }

    private static class JobTask extends AsyncTask<JobParameters, Void, JobParameters> {
        private final JobService jobService;
        private Context context;
        private String app_key;
        private String uuid;
        private String url;
        public JobTask(JobService jobService,Context context,Bundle extra) {
            this.jobService = jobService;
            this.context = context;
            this.app_key = extra.getString("app_key", "");
            this.uuid = extra.getString("uuid","");
            this.url = extra.getString("url","");

        }

        @Override
        protected JobParameters doInBackground(JobParameters... params) {

            RequestParams rparam = new RequestParams();
            rparam.add("uuid",this.uuid);
            rparam.add("app_key",this.app_key);

            JupiterHttpClient.post(url, rparam, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("response", response.toString());
                    try {
                        if (response.getBoolean("status")){
                            JSONArray news_list = response.getJSONArray("result");
                            for(int i = 0;i < news_list.length();i++) {
                                JSONObject news = news_list.getJSONObject(i);
                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                                Date d1 = format.parse(news.getString("publish_date"));
                                CachedNews cn = new CachedNews();
                                cn.setTitle(news.getString("title"));
                                cn.setContent(news.getString("content"));
                                cn.setCategory(news.getString("category"));
                                Log.d("Server Category",news.getString("category"));
                                cn.setDescription(news.getString("description"));
                                cn.setCreateDate(new Date());
                                cn.setImgUrl(news.getString("img_url"));
                                cn.setViewed(false);
                                cn.setSent(false);
                                cn.setStaticURL(news.getString("staticURL"));
                                cn.setPublishDate(d1);
                                cn.save();
                                Log.d("Saved", Long.toString(cn.getId()));
                            }
                        }else if (response.getInt("error")==403){
                            SharedPreferences sp = jobService.getApplication().
                                    getSharedPreferences(jobService.getString(R.string.sharedpref),Context.MODE_MULTI_PROCESS);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putBoolean(jobService.getString(R.string.islogin), false);
                            editor.commit();
                            Log.d("Server","Please Login");
                            //JobScheduler scheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                            //scheduler.cancel(1000);

                        }

                    }
                    catch (JSONException ex){
                        ex.printStackTrace();
                    }
                    catch (ParseException ex){
                        ex.printStackTrace();
                    }


                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("response", Integer.toString(statusCode));
                }
            });
            return params[0];
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            jobService.jobFinished(jobParameters, false);
        }
    }

}