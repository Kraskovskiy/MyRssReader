package com.kab.myrssreader;

import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.RemoteViews;

import com.kab.myrssreader.data.DbFeed;
import com.kab.myrssreader.data.Entry;
import com.kab.myrssreader.data.SharedPreferencesControl;
import com.kab.myrssreader.sync.UpdateJob;
import com.kab.myrssreader.widget.RssWidgetProvider;
import com.kab.myrssreader.widget.WidgetService;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.kab.myrssreader.widget.RssWidgetProvider.ACTION_MAIN;
import static com.kab.myrssreader.widget.RssWidgetProvider.ACTION_NEXT;
import static com.kab.myrssreader.widget.RssWidgetProvider.ACTION_PREV;

/**
 * Created by Kraskovskiy on 19.09.16.
 */
public class Utility {
    private Utility() {}

    public static synchronized void readRssFeed(final Context context, final String link) {
        if (isNetworkAvailable(context)) {
            Log.e("Utility", "readRssFeed: " + "isNetworkAvailable");
            new Thread(new Runnable() {
                public void run() {
                    try {
                        URL url = new URL(link);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        long dateTime = urlConnection.getLastModified();

                        if (isOldFeed(context, dateTime)) {
                            Log.e("Utility", "readRssFeed: " + "isOldFeed");

                            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
                            XmlPullParser myParser = xmlFactoryObject.newPullParser();
                            myParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                            myParser.setInput(urlConnection.getInputStream(), null);

                            saveFeedRssInDb(context, parseXml(myParser));
                            saveNewFeed(context, dateTime);

                        } else {
                            Log.e("Utility", "readRssFeed: " + "isOldFeed = false");
                        }

                        urlConnection.disconnect();

                    } catch (XmlPullParserException | IOException e) {
                        Log.e("Utility", "run: " + e.getMessage());
                    }

                }
            }).start();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static List<Entry> parseXml(XmlPullParser myParser) {
        int event;
        String text = null;
        List<Entry> xmlEntry = new ArrayList<>();
        xmlEntry.add(new Entry());

        try {
            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();

                switch (event) {
                    case XmlPullParser.START_TAG:
                        if (name.equals("item")) {
                            xmlEntry.add(new Entry());
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (name.equals("title")) {
                            xmlEntry.get(xmlEntry.size() - 1).setTitle(text);
                        }
                        if (name.equals("link")) {
                            xmlEntry.get(xmlEntry.size() - 1).setLink(text);
                        }
                        if (name.equals("description")) {
                            xmlEntry.get(xmlEntry.size() - 1).setDescription(text);
                        }
                        break;
                }
                event = myParser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return xmlEntry;
    }

    public static void saveFeedRssInDb(Context context, List<Entry> entries) {
        if (entries.size() > 0) {
            SharedPreferencesControl SP = new SharedPreferencesControl(context);

            DbFeed db = new DbFeed(context);
            db.open();
            db.dbTrunc();

            for (Entry e : entries) {
                db.append(e);
            }

            SP.saveLengthRss(db.getCount());
            SP.saveIdRssItem(db.getCount());
            db.close();
        }
        updateWidget(context, 0);
    }

    public static void clearOldRss(Context context) {
        DbFeed db = new DbFeed(context);
        db.open();
        db.dbTrunc();
        db.close();

        saveNewFeed(context,0);

        updateWidget(context, 0);
    }

    public static void updateWidget(Context context, int widgetID) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

        Entry entry = Utility.getEntryFromDb(context, Utility.getIdEntry(context));
        if (!entry.getTitle().equals("") && !entry.getDescription().equals("")) {
            remoteViews.setTextViewText(R.id.text_widget_title, htmlToTextView(entry.getTitle()));
            remoteViews.setTextViewText(R.id.text_widget_body, htmlToTextView(entry.getDescription()));
        } else {
            if (!isNetworkAvailable(context)) {
                remoteViews.setTextViewText(R.id.text_widget_title, context.getResources().getString(R.string.app_name));
                remoteViews.setTextViewText(R.id.text_widget_body, context.getResources().getString(R.string.error_load_network_problem));
            } else {
                remoteViews.setTextViewText(R.id.text_widget_title, context.getResources().getString(R.string.app_name));
                remoteViews.setTextViewText(R.id.text_widget_body, context.getResources().getString(R.string.error_load_url_problem));
            }
        }

        Intent clickIntent = new Intent(context, RssWidgetProvider.class);
        clickIntent.setAction(ACTION_PREV);
        clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, widgetID, clickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.image_prev, pIntent);

        clickIntent.setAction(ACTION_MAIN);
        clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        pIntent = PendingIntent.getBroadcast(context, widgetID, clickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.linear_layout_widget_body, pIntent);

        clickIntent.setAction(ACTION_NEXT);
        clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        pIntent = PendingIntent.getBroadcast(context, widgetID, clickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.image_next, pIntent);

        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName widgetCompName = new ComponentName(context, RssWidgetProvider.class);
        manager.updateAppWidget(widgetCompName, remoteViews);
    }

    public static void startServiceIfStopped(Context context) {
        if (WidgetService.sState == WidgetService.INACTIVE) {
            Intent widgetService = new Intent(context, WidgetService.class);
            context.startService(widgetService);
        }
    }

    public static void stopServiceIfRunning(Context context) {
        if (WidgetService.sState == WidgetService.ACTIVE) {
            Intent widgetService = new Intent(context, WidgetService.class);
            context.stopService(widgetService);
        }
    }

    @NonNull
    public static Boolean isOldFeed(Context context, long date) {
        SharedPreferencesControl SP = new SharedPreferencesControl(context);
        return (SP.getLastDate(13) != date);
    }

    public static void saveNewFeed(Context context, long date) {
        SharedPreferencesControl SP = new SharedPreferencesControl(context);
        SP.saveLastDate(date);
    }

    public static String getRssFeedUrl(Context context) {
        SharedPreferencesControl SP = new SharedPreferencesControl(context);
        return SP.getRssUrl("");//default values
    }

    public static Entry getEntryFromDb(Context context, int id) {
        DbFeed db = new DbFeed(context);
        Entry entry;

        db.open();

        if (db.getCount() > 0) {
            entry = db.readItem(id);
        } else {
            entry = new Entry();
        }

        db.close();
        return entry;
    }

    public static int getIdEntry(Context context) {
        SharedPreferencesControl SP = new SharedPreferencesControl(context);
        return SP.getIdRssItem(1);
    }

    public static void incIdEntry(Context context) {
        SharedPreferencesControl SP = new SharedPreferencesControl(context);
        if (SP.getIdRssItem(1) < SP.getLengthRss(0)) {
            SP.saveIdRssItem(SP.getIdRssItem(1) + 1);
        } else {
            SP.saveIdRssItem(1);
        }
    }

    public static void decIdEntry(Context context) {
        SharedPreferencesControl SP = new SharedPreferencesControl(context);
        if (SP.getIdRssItem(1) > 1) {
            SP.saveIdRssItem(SP.getIdRssItem(1) - 1);
        } else {
            SP.saveIdRssItem(SP.getLengthRss(1));
        }
    }

    public static void openInBrowser(Context context, int id) {
        Intent intentBrowser = new Intent(Intent.ACTION_VIEW);
        intentBrowser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!getEntryFromDb(context, id).getLink().equals("")&&getEntryFromDb(context, id).getLink().length()>3) {
            intentBrowser.setData(Uri.parse(getEntryFromDb(context, id).getLink()));
            context.startActivity(intentBrowser);
        } else {
            startMainActivity(context);
        }
    }

    public static void startMainActivity(Context context) {
        Intent intentActivity = new Intent(context, MainActivity.class);
        intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentActivity);
    }

    public static void startJobScheduleApi21(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler mJobScheduler = (JobScheduler)
                    context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            JobInfo.Builder builder = null;

            builder = new JobInfo.Builder(UpdateJob.JOB_ID,
                    new ComponentName(context, UpdateJob.class));

            builder.setPeriodic(60000); // in every 1 min
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // only when network is available

            if (mJobScheduler.schedule(builder.build()) <= 0) {
                //error, cant be scheduled
            }

            //later e.g. when update is disabled
            // mJobScheduler.cancel(UpdateJob.JOB_ID);
        }
    }

    public static Spanned htmlToTextView(String string) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(string);
        }
    }
}
