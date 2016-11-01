package com.quanlt.vietcomic.ui.grid;

import android.database.Cursor;
import android.net.Uri;

import com.quanlt.vietcomic.data.ComicContract;


public class FavoriteFragment extends BaseFragment {

    @Override
    protected Uri getContentUri() {
        return ComicContract.FavoriteEntry.CONTENT_URI;
    }

    @Override
    protected void onCursorLoaded(Cursor data) {
        getAdapter().swapCursor(data);
    }

    @Override
    protected void onRefreshAction() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onComicGridInitFinished() {

    }
}
