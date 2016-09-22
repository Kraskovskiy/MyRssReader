package com.kab.myrssreader.data;

/**
 * Created by Kraskovskiy on 18.09.2016.
 */
public class Entry {
    private String mTitle;
    private String mLink;
    private String mDescription;

    public Entry() {
        mTitle = "";
        mDescription = "";
        mLink = "";
    }

    public Entry(String title, String description, String link) {
        mTitle = title;
        mDescription = description;
        mLink = link;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "mTitle='" + mTitle + '\'' +
                ", mLink='" + mLink + '\'' +
                ", mDescription='" + mDescription + '\'' +
                '}';
    }
}
