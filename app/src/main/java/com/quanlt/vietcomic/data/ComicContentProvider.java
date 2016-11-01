package com.quanlt.vietcomic.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import static com.quanlt.vietcomic.data.ComicContract.CONTENT_AUTHORITY;
import static com.quanlt.vietcomic.data.ComicContract.CategoryEntry;
import static com.quanlt.vietcomic.data.ComicContract.ComicEntry;
import static com.quanlt.vietcomic.data.ComicContract.FavoriteEntry;
import static com.quanlt.vietcomic.data.ComicContract.PATH_CATEGORY;
import static com.quanlt.vietcomic.data.ComicContract.PATH_COMIC;
import static com.quanlt.vietcomic.data.ComicContract.PATH_FAVORITE;

public class ComicContentProvider extends ContentProvider {

    private static final int COMICS = 100;
    private static final int COMIC_BY_ID = 101;
    private static final int COMICS_BY_CATEGORY = 102;
    private static final int COMICS_BY_AUTHOR = 103;
    private static final int CATEGORIES = 200;
    private static final int FAVORITES = 300;
    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    private ComicDbHelper dbHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, PATH_COMIC, COMICS);
        uriMatcher.addURI(authority, PATH_COMIC + "/*", COMIC_BY_ID);
        uriMatcher.addURI(authority, PATH_COMIC + "?author=*", COMICS_BY_AUTHOR);
        uriMatcher.addURI(authority, PATH_COMIC + "?category=*", COMICS_BY_CATEGORY);
        uriMatcher.addURI(authority, PATH_CATEGORY, CATEGORIES);
        uriMatcher.addURI(authority, PATH_FAVORITE, FAVORITES);
        return uriMatcher;
    }


    public ComicContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = URI_MATCHER.match(uri);
        int rowDeleted;
        switch (match) {
            case FAVORITES:
                rowDeleted = dbHelper.getWritableDatabase().delete(FavoriteEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        if (rowDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowDeleted;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case COMICS:
                return ComicEntry.CONTENT_DIR_TYPE;
            case COMIC_BY_ID:
                return ComicEntry.CONTENT_ITEM_TYPE;
            case COMICS_BY_CATEGORY:
                return ComicEntry.CONTENT_DIR_TYPE;
            case COMICS_BY_AUTHOR:
                return ComicEntry.CONTENT_DIR_TYPE;
            case CATEGORIES:
                return CategoryEntry.CONTENT_DIR_TYPE;
            case FAVORITES:
                return FavoriteEntry.CONTENT_DIR_TYPE;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }


    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        Uri returnUri;
        long id;
        switch (match) {
            case COMICS:
                id = db.insertWithOnConflict(ComicEntry.TABLE_NAME, null,
                        values, SQLiteDatabase.CONFLICT_REPLACE);
                if (id > 0) {
                    returnUri = ComicEntry.buildComicWithId(values.getAsString(ComicEntry._ID));
                } else {
                    throw new SQLiteException("failed to insert into " + uri);
                }
                break;
            case CATEGORIES:
                id = db.insertWithOnConflict(ComicEntry.TABLE_NAME, null,
                        values, SQLiteDatabase.CONFLICT_IGNORE);
                if (id > 0) {
                    returnUri = CategoryEntry.buildCategoryUri(id);
                } else {
                    throw new SQLiteException("failed to insert into " + uri);
                }
                break;
            case FAVORITES:
                id = db.insert(FavoriteEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = FavoriteEntry.CONTENT_URI;
                } else {
                    throw new SQLiteException("failed to insert into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        getContext().getContentResolver().notifyChange(returnUri, null);
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new ComicDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int match = URI_MATCHER.match(uri);
        Cursor cursor;
        switch (match) {
            case COMICS:
                cursor = dbHelper.getReadableDatabase().query(
                        ComicEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        ComicEntry.COLUMN_UPDATE_TIME + " DESC");
                break;
            case COMICS_BY_AUTHOR:
                String author = ComicEntry.getAuthorFromUri(uri);
                cursor = dbHelper.getReadableDatabase().query(
                        ComicEntry.TABLE_NAME,
                        projection,
                        ComicEntry.COLUMN_AUTHOR + " LIKE ?",
                        new String[]{"%" + author + "%"},
                        null,
                        null,
                        ComicEntry.COLUMN_UPDATE_TIME + " DESC");
                break;
            case COMICS_BY_CATEGORY:
                String category = ComicEntry.getCategoryFromUri(uri);
                cursor = dbHelper.getReadableDatabase().query(
                        ComicEntry.TABLE_NAME,
                        projection,
                        ComicEntry.COLUMN_CATEGORIES + " LIKE ?",
                        new String[]{"%" + category + "%"},
                        null,
                        null,
                        ComicEntry.COLUMN_UPDATE_TIME + " DESC");
                break;
            case CATEGORIES:
                cursor = dbHelper.getReadableDatabase().query(
                        CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITES:
                cursor = getComicsFromFavorite(projection, selection, selectionArgs, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor getComicsFromFavorite(String[] projection, String selection,
                                         String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FavoriteEntry.TABLE_NAME + " INNER JOIN " + ComicEntry.TABLE_NAME + " ON " + FavoriteEntry.TABLE_NAME + "." + ComicContract.COLUMN_COMIC_ID + "=" +
                ComicEntry.TABLE_NAME + "." + ComicEntry._ID);
        return builder.query(dbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
