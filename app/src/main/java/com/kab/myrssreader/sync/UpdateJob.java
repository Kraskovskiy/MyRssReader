package com.kab.myrssreader.sync;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;

import com.kab.myrssreader.Utility;

/**
 * Created by Kraskovskiy on 19.09.16.
 */
//TODO implement for api 21+
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class UpdateJob extends JobService {
    public static final int JOB_ID=123321;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Utility.readRssFeed(getApplicationContext(), Utility.getRssFeedUrl(getApplicationContext()));
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
