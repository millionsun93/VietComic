package com.quanlt.vietcomic.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.quanlt.vietcomic.api.ComicNetworkService;
import com.quanlt.vietcomic.data.ComicContract.FavoriteEntry;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ComicService {
    private static final int PAGE_SIZE = 20;
    public static final String BROADCAST_UPDATE_FINISHED = "update_finished";
    public static final String BROADCAST_UPDATE_SUCCESS = "update_successful";
    private static final String TAG = ComicService.class.getSimpleName();
    private final Context context;
    private volatile boolean loading = false;
    private ComicNetworkService comicNetworkService;

    @Inject
    public ComicService(Context context, ComicNetworkService comicNetworkService) {
        this.context = context.getApplicationContext();
        this.comicNetworkService = comicNetworkService;
    }

    public boolean isLoading() {
        return loading;
    }

    public void refreshComic() {
        if (isLoading()) {
            return;
        }
        loading = true;
        callLoadComic(null);
    }

    public void loadMoreComic() {
        if (loading) {
            return;
        }
        loading = true;
        Uri uri = ComicContract.ComicEntry.CONTENT_URI;
        callLoadComic(getCurrentPage(uri) + 1);
    }

    public void addToFavorite(Comic comic) {
        ContentValues values = new ContentValues();
        values.put(ComicContract.COLUMN_COMIC_ID, comic.getId());
        context.getContentResolver().insert(FavoriteEntry.CONTENT_URI, values);
    }

    public void removeFromFavorite(Comic comic) {
        context.getContentResolver().delete(FavoriteEntry.CONTENT_URI, ComicContract.COLUMN_COMIC_ID + " = ?", new String[]{comic.getId()});
    }

    public boolean isFavorite(Comic comic) {
        boolean favorite = false;
        Cursor cursor = context.getContentResolver().query(FavoriteEntry.CONTENT_URI,
                null,
                ComicContract.COLUMN_COMIC_ID + " = ?",
                new String[]{comic.getId()},
                null);
        if (cursor != null) {
            favorite = cursor.getCount() != 0;
            cursor.close();
        }
        return favorite;
    }

    private void callLoadComic(Integer page) {
        comicNetworkService.getComic(page)
                .subscribeOn(Schedulers.io())
                .map(response -> response.getData())
                .flatMap(comics -> Observable.from(comics))
                .map(comic -> saveComic(comic))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Uri>() {
                    @Override
                    public void onCompleted() {
                        loading = false;
                        sendUpdateFinishedBroadCast(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                        loading = false;
                        sendUpdateFinishedBroadCast(false);
                    }

                    @Override
                    public void onNext(Uri uri) {

                    }
                });

    }

    private Uri saveComic(Comic comic) {
        return context.getContentResolver().insert(ComicContract.ComicEntry.CONTENT_URI, comic.toContentValues());
    }

    private int getCurrentPage(Uri uri) {
        Cursor comics = context.getContentResolver().query(
                uri, null, null, null, null
        );
        int currentPage = 1;
        if (comics != null) {
            currentPage = (comics.getCount() - 1) / PAGE_SIZE + 1;
            comics.close();
        }
        return currentPage;
    }

    private void sendUpdateFinishedBroadCast(boolean successfulUpdate) {
        Intent intent = new Intent(BROADCAST_UPDATE_FINISHED);
        intent.putExtra(BROADCAST_UPDATE_SUCCESS, successfulUpdate);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
