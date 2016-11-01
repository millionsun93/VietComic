package com.quanlt.vietcomic.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Chapter implements Parcelable {
    private String title;
    private List<String> urls;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeStringList(this.urls);
    }

    public Chapter() {
    }

    protected Chapter(Parcel in) {
        this.title = in.readString();
        this.urls = in.createStringArrayList();
    }

    public static final Parcelable.Creator<Chapter> CREATOR = new Parcelable.Creator<Chapter>() {
        @Override
        public Chapter createFromParcel(Parcel source) {
            return new Chapter(source);
        }

        @Override
        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        return this.title.equals(((Chapter) obj).getTitle());
    }
}
