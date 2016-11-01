package com.quanlt.vietcomic.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class TestUtilities {
    public static final String COMIC_ID = "123456";
    public static final String AUTHOR = "John Wick";
    public static final String CATEGORY = "Action";
    public static final Calendar calendar = Calendar.getInstance();

    public static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue(error, valueCursor.moveToFirst());
        validateCurrentRecord(valueCursor, expectedValues);
        valueCursor.close();
    }

    private static void validateCurrentRecord(Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column " + columnName + " not found", idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value " + columnName + " did not match ", expectedValue, valueCursor.getString(idx));
        }
    }

    public static ContentValues provideComicContentValue() {
        ContentValues values = new ContentValues();
        values.put(ComicContract.ComicEntry._ID, COMIC_ID);
        values.put(ComicContract.ComicEntry.COLUMN_TITLE, "Avenger");
        values.put(ComicContract.ComicEntry.COLUMN_STATUS, "Updating");
        values.put(ComicContract.ComicEntry.COLUMN_SOURCE, "MarvelComic");
        values.put(ComicContract.ComicEntry.COLUMN_THUMBNAIL, "http://vietcomic.net/files/files/61z7ktq5WoL__AC_UL320_SR204%2C320_.jpg");
        values.put(ComicContract.ComicEntry.COLUMN_DESCRIPTION, "Description");
        values.put(ComicContract.ComicEntry.COLUMN_UPDATE_TIME, calendar.getTimeInMillis());
        values.put(ComicContract.ComicEntry.COLUMN_LATEST_CHAPTER, "Chapter 4");
        values.put(ComicContract.ComicEntry.COLUMN_AUTHOR, "John Wick;Stan Lee;I don't know;This is a test");
        values.put(ComicContract.ComicEntry.COLUMN_CATEGORIES, "Action;Thriller;Fantasy");
        return values;
    }
}
