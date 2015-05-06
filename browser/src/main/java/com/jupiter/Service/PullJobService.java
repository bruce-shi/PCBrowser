package com.jupiter.Service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import com.jupiter.pcbrowser.MainActivity;

import java.util.Calendar;
import java.util.LinkedList;

/**
 * Created by lovew_000 on 2015/5/7.
 */
public class PullJobService extends JobService {
    private static final String TAG = "PullJobService";

    @Override
    public boolean onStartJob(JobParameters params) {
        new JobTask(this,getApplicationContext()).execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return false;
    }

    private static class JobTask extends AsyncTask<JobParameters, Void, JobParameters> {
        private final JobService jobService;
        private Context context;
        public JobTask(JobService jobService,Context context) {
            this.jobService = jobService;
            this.context = context;
        }

        @Override
        protected JobParameters doInBackground(JobParameters... params) {
            Log.d("Job","Do job");
            return params[0];
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            jobService.jobFinished(jobParameters, false);
        }
    }

}