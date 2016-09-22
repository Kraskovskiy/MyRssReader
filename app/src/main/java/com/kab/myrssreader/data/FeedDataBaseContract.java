package com.kab.myrssreader.data;

import android.provider.BaseColumns;

/**
 * Created by Kraskovskiy on 19.09.16.
 */
public class FeedDataBaseContract {
    private FeedDataBaseContract() {}

    static abstract class FeedEntry implements BaseColumns {
        static final String DATABASE_NAME = "myRssReaderDb";
        static final String TABLE_NAME = "entry";
        static final String COLUMN_ID = "_id";
        static final String COLUMN_TITLE = "name";
        static final String COLUMN_URL = "url";
        static final String COLUMN_DESCRIPTION = "description";
    }
}

