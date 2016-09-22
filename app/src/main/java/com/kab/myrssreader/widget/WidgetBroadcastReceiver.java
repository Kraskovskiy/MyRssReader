package com.kab.myrssreader.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kab.myrssreader.Utility;

/**
 * Created by Kraskovskiy on 19.09.16.
 */
public class WidgetBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        switch (action) {
            case RssWidgetProvider.ACTION_NEXT:
                Utility.incIdEntry(context);
                Utility.updateWidget(context, 0);
                break;
            case RssWidgetProvider.ACTION_PREV:
                Utility.decIdEntry(context);
                Utility.updateWidget(context, 0);
                break;
            case RssWidgetProvider.ACTION_MAIN:
                Utility.openInBrowser(context, Utility.getIdEntry(context));
                Utility.updateWidget(context, 0);
                break;
        }
    }
}
