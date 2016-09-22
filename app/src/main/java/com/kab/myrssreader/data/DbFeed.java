package com.kab.myrssreader.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kab.myrssreader.data.FeedDataBaseContract.*;

/**
 * Created by Kraskovskiy on 19.09.16.
 */
public class DbFeed {
    private final Context mContext;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDb;

    public DbFeed(Context context) {
        mContext = context;
    }

    public void open() {
        mDBHelper = new DBHelper(mContext);
        mDb = mDBHelper.getWritableDatabase();
    }

    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

    public Entry readItem(int id) {
        Cursor cursor = mDb.query(FeedEntry.TABLE_NAME, null, FeedEntry.COLUMN_ID + " = " + id, null, null, null, null);
        if (cursor.moveToFirst()) {
            int itemTitle_index = cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_TITLE);
            int itemUrl_index = cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_URL);
            int itemDescription_index = cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_DESCRIPTION);

            Entry entry = new Entry(cursor.getString(itemTitle_index), cursor.getString(itemDescription_index),
                    cursor.getString(itemUrl_index));

            cursor.close();
            return entry;
        } else {
            cursor.close();
            return null;
        }
    }

    public void append(Entry entry) {
        ContentValues cv = new ContentValues();
        cv.put(FeedEntry.COLUMN_TITLE, entry.getTitle());
        cv.put(FeedEntry.COLUMN_URL, entry.getLink());
        cv.put(FeedEntry.COLUMN_DESCRIPTION, entry.getDescription());

        mDb.insert(FeedEntry.TABLE_NAME, null, cv);
        mContext.getContentResolver().notifyChange(DBHelper.URI_TABLE_NAME, null);
    }

    public int getCount() {
        String countQuery = "SELECT  * FROM " + FeedEntry.TABLE_NAME;
        Cursor cursor = mDb.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public void dbTrunc() {
        mDb.execSQL("DROP TABLE " + FeedEntry.TABLE_NAME);
        mDb.execSQL(DBHelper.DB_CREATE_STRING);
        mContext.getContentResolver().notifyChange(DBHelper.URI_TABLE_NAME, null);
    }
}