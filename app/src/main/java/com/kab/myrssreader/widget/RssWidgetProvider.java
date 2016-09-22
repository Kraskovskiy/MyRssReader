package com.kab.myrssreader.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import com.kab.myrssreader.data.SharedPreferencesControl;
import com.kab.myrssreader.Utility;


/**
 * Created by Kraskovskiy on 18.09.2016.
 */
public class RssWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_PREV = "rssPrevRead";
    public static final String ACTION_NEXT = "rssNextRead";
    public static final String ACTION_MAIN = "rssMainAction";
    private SharedPreferencesControl mSharedPref;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        mSharedPref = new SharedPreferencesControl(context);
        mSharedPref.setEnabledWidget(true);

        Utility.startServiceIfStopped(context);

        if (mSharedPref.getRssUrl("").equals("")) {
            Utility.startMainActivity(context);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Utility.startServiceIfStopped(context);

        if (intent.getAction().equals(ACTION_PREV)) {
            Intent intentAction = new Intent(ACTION_PREV);
            intentAction.setAction(ACTION_PREV);
            context.sendBroadcast(intentAction);
        }

        if (intent.getAction().equals(ACTION_MAIN)) {
            Intent intentAction = new Intent(ACTION_MAIN);
            intentAction.setAction(ACTION_MAIN);
            context.sendBroadcast(intentAction);
        }

        if (intent.getAction().equals(ACTION_NEXT)) {
            Intent intentAction = new Intent(ACTION_NEXT);
            intentAction.setAction(ACTION_NEXT);
            context.sendBroadcast(intentAction);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int id : appWidgetIds) {
            Utility.updateWidget(context, id);
        }

        Utility.startServiceIfStopped(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        if (mSharedPref != null) {
            mSharedPref.setEnabledWidget(false);
        } else {
            mSharedPref = new SharedPreferencesControl(context);
            mSharedPref.setEnabledWidget(false);
        }

        Utility.stopServiceIfRunning(context);
    }
}
