package com.kab.myrssreader.widget;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.kab.myrssreader.sync.UpdateDataScheduler;

/**
 * Created by Kraskovskiy on 19.09.16.
 */
public class WidgetService extends Service {
    public static final int INACTIVE = 0;
    public static final int ACTIVE = 1;
    public static int sState;
    private static WidgetBroadcastReceiver sBroadcastReceiver;
    private static Boolean sBroadcastAlive = false;
    private UpdateDataScheduler mSchedule = new UpdateDataScheduler();

    static {
        sState = INACTIVE;
    }

    private static void createBroadcast(Context context) {
        if (!sBroadcastAlive) {
            sBroadcastReceiver = new WidgetBroadcastReceiver();

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

            intentFilter.addAction(RssWidgetProvider.ACTION_PREV);
            intentFilter.addAction(RssWidgetProvider.ACTION_NEXT);
            intentFilter.addAction(RssWidgetProvider.ACTION_MAIN);

            context.registerReceiver(sBroadcastReceiver, intentFilter);
            sBroadcastAlive = true;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sState = ACTIVE;
        createBroadcast(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sState = ACTIVE;
        createBroadcast(getApplicationContext());

        mSchedule.readRssFeedScheduler(getApplicationContext());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        sState = INACTIVE;

        try {
            getApplicationContext().unregisterReceiver(sBroadcastReceiver);
            sBroadcastAlive = false;
        } catch (Exception e) {
            sBroadcastAlive = false;
        }

        mSchedule.stopScheduler();
        super.onDestroy();
    }
}