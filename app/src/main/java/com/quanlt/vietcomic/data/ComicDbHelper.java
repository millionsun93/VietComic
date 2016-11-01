package com.quanlt.vietcomic.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ComicDbHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "vietcomic";
    private static final int DATABASE_VERSION = 1;

    public ComicDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ComicContract.ComicEntry.SQL_CREATE_TABLE);
        db.execSQL(ComicContract.CategoryEntry.SQL_CREATE_TABLE);
        db.execSQL(ComicContract.FavoriteEntry.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ComicContract.ComicEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ComicContract.CategoryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ComicContract.FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}

