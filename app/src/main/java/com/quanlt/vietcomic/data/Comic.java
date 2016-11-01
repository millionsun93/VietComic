package com.quanlt.vietcomic.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Movie;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.quanlt.vietcomic.data.ComicContract.ComicEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Comic implements Parcelable {
    @SerializedName("_id")
    private String id;
    private String title;
    private String status;
    private String source;
    private String description;
    private String thumbnail;
    private String latestChapter;
    private Date updateTime;
    private int viewers;
    private List<Chapter> chapters;
    private List<String> authors;
    private List<String> categories;

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ComicEntry._ID, id);
        contentValues.put(ComicEntry.COLUMN_TITLE, title);
        contentValues.put(ComicEntry.COLUMN_STATUS, status);
        contentValues.put(ComicEntry.COLUMN_SOURCE, source);
        contentValues.put(ComicEntry.COLUMN_VIEW_COUNT, viewers);
        contentValues.put(ComicEntry.COLUMN_THUMBNAIL, thumbnail);
        contentValues.put(ComicEntry.COLUMN_DESCRIPTION, description);
        contentValues.put(ComicEntry.COLUMN_UPDATE_TIME, updateTime.getTime());
        contentValues.put(ComicEntry.COLUMN_LATEST_CHAPTER, latestChapter);
        contentValues.put(ComicEntry.COLUMN_AUTHOR, TextUtils.join(";", authors));
        contentValues.put(ComicEntry.COLUMN_CATEGORIES, TextUtils.join(";", categories));
        return contentValues;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getLastestChapter() {
        return latestChapter;
    }

    public void setLastestChapter(String lastestChapter) {
        this.latestChapter = lastestChapter;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "Comic{" +
                "categories=" + categories +
                ", authors=" + authors +
                ", chapters=" + chapters +
                ", updateTime=" + updateTime +
                ", latestChapter='" + latestChapter + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", description='" + description + '\'' +
                ", source='" + source + '\'' +
                ", status='" + status + '\'' +
                ", title='" + title + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public String getLatestChapter() {
        return latestChapter;
    }

    public void setLatestChapter(String latestChapter) {
        this.latestChapter = latestChapter;
    }

    public int getViewers() {
        return viewers;
    }

    public void setViewers(int viewers) {
        this.viewers = viewers;
    }

    public static Comic fromCursor(Cursor cursor) {
        Comic comic = new Comic();
        comic.setId(cursor.getString(cursor.getColumnIndex(ComicEntry._ID)));
        comic.setTitle(cursor.getString(cursor.getColumnIndex(ComicEntry.COLUMN_TITLE)));
        comic.setStatus(cursor.getString(cursor.getColumnIndex(ComicEntry.COLUMN_STATUS)));
        comic.setSource(cursor.getString(cursor.getColumnIndex(ComicEntry.COLUMN_SOURCE)));
        comic.setThumbnail(cursor.getString(cursor.getColumnIndex(ComicEntry.COLUMN_THUMBNAIL)));
        comic.setViewers(cursor.getInt(cursor.getColumnIndex(ComicEntry.COLUMN_VIEW_COUNT)));
        comic.setDescription(cursor.getString(cursor.getColumnIndex(ComicEntry.COLUMN_DESCRIPTION)));
        comic.setLastestChapter(cursor.getString(cursor.getColumnIndex(ComicEntry.COLUMN_LATEST_CHAPTER)));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ComicEntry.COLUMN_UPDATE_TIME)));
        comic.setUpdateTime(calendar.getTime());
        comic.setAuthors(Arrays.asList(TextUtils.split(cursor.getString(cursor.getColumnIndex(ComicEntry.COLUMN_AUTHOR)), ";")));
        comic.setCategories(Arrays.asList(TextUtils.split(cursor.getString(cursor.getColumnIndex(ComicEntry.COLUMN_CATEGORIES)), ";")));
        return comic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.status);
        dest.writeString(this.source);
        dest.writeString(this.description);
        dest.writeString(this.thumbnail);
        dest.writeInt(this.viewers);
        dest.writeString(this.latestChapter);
        dest.writeLong(this.updateTime != null ? this.updateTime.getTime() : -1);
        dest.writeList(this.chapters);
        dest.writeStringList(this.authors);
        dest.writeStringList(this.categories);
    }

    public Comic() {
    }

    protected Comic(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.status = in.readString();
        this.source = in.readString();
        this.description = in.readString();
        this.thumbnail = in.readString();
        this.viewers = in.readInt();
        this.latestChapter = in.readString();
        long tmpUpdateTime = in.readLong();
        this.updateTime = tmpUpdateTime == -1 ? null : new Date(tmpUpdateTime);
        this.chapters = new ArrayList<>();
        in.readList(this.chapters, Chapter.class.getClassLoader());
        this.authors = in.createStringArrayList();
        this.categories = in.createStringArrayList();
    }

    public static final Parcelable.Creator<Comic> CREATOR = new Parcelable.Creator<Comic>() {
        @Override
        public Comic createFromParcel(Parcel source) {
            return new Comic(source);
        }

        @Override
        public Comic[] newArray(int size) {
            return new Comic[size];
        }
    };
}
