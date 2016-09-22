package com.kab.myrssreader.sync;

import android.content.Context;
import android.util.Log;

import com.kab.myrssreader.Utility;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by Kraskovskiy on 21.09.16.
 */

public class UpdateDataScheduler {
    private static final int UPDATE_TIME_SEC = 60;
    private static Boolean sIsCanceled = false;
    private static Boolean sIsRunning = false;
    private ScheduledExecutorService mScheduler = Executors.newScheduledThreadPool(1);

    public void readRssFeedScheduler(final Context context) {
        if (!sIsRunning) {
            sIsRunning = true;
            mScheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (!sIsCanceled) {
                        Utility.readRssFeed(context, Utility.getRssFeedUrl(context));
                        Log.e("UpdateDataScheduler", "run: " + "readRssFeedScheduler");
                    } else {
                        mScheduler.shutdownNow();
                        sIsCanceled = false;
                        sIsRunning = false;
                    }
                }
            }, 0, UPDATE_TIME_SEC, SECONDS);
        }
    }

    public void stopScheduler() {
        sIsCanceled = true;
        sIsRunning = false;
    }
}
