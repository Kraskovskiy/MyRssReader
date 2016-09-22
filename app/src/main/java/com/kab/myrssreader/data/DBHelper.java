package com.kab.myrssreader.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import com.kab.myrssreader.data.FeedDataBaseContract.*;

/**
 * Created by Kraskovskiy on 19.09.16.
 */
class DBHelper  extends SQLiteOpenHelper {
    static final Uri URI_TABLE_NAME = Uri.parse("sqlite://com.kab.myrssreader/table/" + FeedEntry.TABLE_NAME);
    static final String DB_CREATE_STRING = "create table "+ FeedEntry.TABLE_NAME+ " ("
            + FeedEntry.COLUMN_ID+" integer primary key autoincrement,"
            + FeedEntry.COLUMN_TITLE+" text,"
            + FeedEntry.COLUMN_URL +" text,"
            + FeedEntry.COLUMN_DESCRIPTION +" text"
            +");";

    private static final int NUMBER_OF_VERSION_DB = 1;

    DBHelper(Context context) {
        super(context, FeedEntry.DATABASE_NAME, null, NUMBER_OF_VERSION_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE_STRING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
