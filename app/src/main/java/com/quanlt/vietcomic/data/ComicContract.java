package com.quanlt.vietcomic.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.sax.RootElement;

public class ComicContract {
    public static final String CONTENT_AUTHORITY = "com.quanlt.vietcomic";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_COMIC = "comics";
    public static final String PATH_CATEGORY = "categories";
    public static final String PATH_FAVORITE = "favorite";
    public static final String COLUMN_COMIC_ID = "comic_id";

    public static final class ComicEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMIC).build();
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_COMIC;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_COMIC;

        public static final String TABLE_NAME = "comics";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_SOURCE = "source";
        public static final String COLUMN_THUMBNAIL = "thumbnail";
        public static final String COLUMN_VIEW_COUNT = "view_count";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_UPDATE_TIME = "update_time";
        public static final String COLUMN_AUTHOR = "authors";
        public static final String COLUMN_CATEGORIES = "categories";
        public static final String COLUMN_LATEST_CHAPTER = "latest_chapter";
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                _ID + " TEXT UNIQUE," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_STATUS + " TEXT," +
                COLUMN_SOURCE + " TEXT," +
                COLUMN_THUMBNAIL + " TEXT," +
                COLUMN_VIEW_COUNT + " INTEGER," +
                COLUMN_UPDATE_TIME + " INTEGER," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_AUTHOR + " TEXT," +
                COLUMN_CATEGORIES + " TEXT," +
                COLUMN_LATEST_CHAPTER + " TEXT," +
                "UNIQUE(" + _ID + "," + COLUMN_UPDATE_TIME + ")" +
                ");";
        private static final String[] COLUMNS = {_ID, COLUMN_TITLE, COLUMN_STATUS, COLUMN_SOURCE, COLUMN_THUMBNAIL,
                COLUMN_VIEW_COUNT, COLUMN_UPDATE_TIME, COLUMN_DESCRIPTION, COLUMN_AUTHOR, COLUMN_CATEGORIES, COLUMN_LATEST_CHAPTER};

        public static final Uri buildComicWithId(String id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(id)
                    .build();
        }

        public static final Uri buildComicWithAuthor(String author) {
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter("author", author)
                    .build();
        }

        public static final Uri buildComicWithCategory(String category) {
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter("category", category)
                    .build();
        }

        public static String[] getColumns() {
            return COLUMNS.clone();
        }

        public static String getAuthorFromUri(Uri uri) {
            return uri.getQueryParameter("author");
        }

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getCategoryFromUri(Uri uri) {
            return uri.getQueryParameter("category");
        }
    }

    public static final class CategoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_TITLE = "title";
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_TITLE + " TEXT UNIQUE);";

        private static final String[] COLUMNS = {_ID, COLUMN_TITLE};

        public static String[] getColumns() {
            return COLUMNS.clone();
        }

        public static Uri buildCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class FavoriteEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_FAVORITE).build();
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        public static final String TABLE_NAME = "favorite";
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_COMIC_ID + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + COLUMN_COMIC_ID + ") REFERENCES " +
                ComicEntry.TABLE_NAME + " (" + ComicEntry._ID + "));";
        private static final String[] COLUMNS = {_ID, COLUMN_COMIC_ID};

        public static String[] getColums() {
            return COLUMNS.clone();
        }
    }
}
