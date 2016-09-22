package com.kab.myrssreader.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Kraskovskiy on 19.09.16.
 */
public class SharedPreferencesControl {
    private static final String APP_PREFERENCES = "settingMyRssReader";
    private SharedPreferences mSettings;

    public SharedPreferencesControl(Context context) {
        mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void saveLastDate(long value) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putLong("saveLastDate", value);
        editor.commit();
    }

    public long getLastDate(long value) {
        return mSettings.getLong("saveLastDate", value);
    }

    public void saveLengthRss(int value) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt("saveLengthRss", value);
        editor.commit();
    }

    public int getLengthRss(int value) {
        return mSettings.getInt("saveLengthRss", value);
    }

    public void saveIdRssItem(int value) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt("saveIdItem", value);
        editor.commit();
    }

    public int getIdRssItem(int value) {
        return mSettings.getInt("saveIdItem", value);
    }

    public void saveRssUrl(String value) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString("saveRssUrl", value.toLowerCase());
        editor.commit();
    }

    public String getRssUrl(String value) {
        return mSettings.getString("saveRssUrl", value);
    }

    public void setEnabledWidget(Boolean value) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean("setEnabledWidget", value).commit();
    }

    public Boolean getEnabledWidget() {
        return mSettings.getBoolean("setEnabledWidget", false);
    }
}
