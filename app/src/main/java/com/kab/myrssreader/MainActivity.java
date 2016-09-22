package com.kab.myrssreader;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kab.myrssreader.data.SharedPreferencesControl;

public class MainActivity extends AppCompatActivity {
    private SharedPreferencesControl mSharedPref;
    private EditText mEditTextRssUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSharedPref = new SharedPreferencesControl(this);

        mEditTextRssUrl = (EditText) findViewById(R.id.edit_text_rss_address);
        if (mSharedPref.getRssUrl("").equals("")) {
            mSharedPref.saveRssUrl("https://techcrunch.com/feed/");
            mEditTextRssUrl.setText(mSharedPref.getRssUrl("https://techcrunch.com/feed/"));
        } else {
            mEditTextRssUrl.setText(mSharedPref.getRssUrl("https://techcrunch.com/feed/"));
        }
        mEditTextRssUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                matcherUrl(editable.toString());
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEditTextRssUrl.getText().toString().equals("")) {
                    mSharedPref.saveRssUrl("https://techcrunch.com/feed/");
                    Toast.makeText(getApplicationContext(), R.string.warning_null_url_message, Toast.LENGTH_SHORT).show();
                    Utility.readRssFeed(getApplicationContext(), Utility.getRssFeedUrl(getApplicationContext()));
                } else {
                    Utility.clearOldRss(getApplicationContext());
                    Utility.readRssFeed(getApplicationContext(), Utility.getRssFeedUrl(getApplicationContext()));
                }
                finish();
            }
        });
    }

    private void matcherUrl(String url) {
        if (Patterns.WEB_URL.matcher(url).matches()) {
            if (url.toLowerCase().startsWith("http://") || url.toLowerCase().startsWith("https://")) {
                mSharedPref.saveRssUrl(url);
            } else {
                mEditTextRssUrl.setError(getString(R.string.error_edit_text_url_protocol));
            }
        } else {
            mEditTextRssUrl.setError(getString(R.string.error_edit_text_incorrect_url));
        }
    }
}
