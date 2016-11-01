package com.quanlt.vietcomic.ui.grid;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.quanlt.vietcomic.ComicApplication;
import com.quanlt.vietcomic.R;
import com.quanlt.vietcomic.api.ComicNetworkService;
import com.quanlt.vietcomic.data.ComicContract;
import com.quanlt.vietcomic.data.ComicService;
import com.quanlt.vietcomic.util.EndlessRecyclerViewOnScrollListener;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ComicFragment extends BaseFragment {

    private static final String TAG = ComicFragment.class.getSimpleName();
    private static final long SEARCH_QUERY_TIMEOUT = 400;
    @Inject
    ComicService comicService;
    @Inject
    ComicNetworkService comicNetworkService;

    private EndlessRecyclerViewOnScrollListener endlessRecyclerViewOnScrollListener;
    private SearchView searchView;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ComicService.BROADCAST_UPDATE_FINISHED)) {
                if (!intent.getBooleanExtra(ComicService.BROADCAST_UPDATE_SUCCESS, true)) {
                    Snackbar.make(mSwipeRefreshLayout, getString(R.string.error_update_failed), Snackbar.LENGTH_LONG).show();
                }
                mSwipeRefreshLayout.setRefreshing(false);
                endlessRecyclerViewOnScrollListener.setLoading(false);
                updateGridLayout();
            }
        }
    };

    public static ComicFragment newInstance() {
        ComicFragment fragment = new ComicFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((ComicApplication) getActivity().getApplicationContext()).getNetworkComponent().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ComicService.BROADCAST_UPDATE_FINISHED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, intentFilter);
        if (endlessRecyclerViewOnScrollListener != null) {
            endlessRecyclerViewOnScrollListener.setLoading(comicService.isLoading());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_comic_grid, menu);
        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
        if (searchViewMenuItem != null) {
            searchView = (SearchView) searchViewMenuItem.getActionView();
            MenuItemCompat.setOnActionExpandListener(searchViewMenuItem,
                    new MenuItemCompat.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem item) {
                            mRecyclerView.setAdapter(null);
                            initComicsGrid();
                            restartLoader();
                            mSwipeRefreshLayout.setEnabled(true);
                            return true;
                        }

                        @Override
                        public boolean onMenuItemActionExpand(MenuItem item) {
                            return true;
                        }
                    });
            setupSearchView();
        }
    }

    private void setupSearchView() {
        if (searchView == null) {
            Log.e(TAG, "SearchView is not initialized");
            return;
        }
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        RxSearchView.queryTextChanges(searchView)
                .debounce(SEARCH_QUERY_TIMEOUT, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .filter(query -> query.length() > 4)
                .doOnNext(query -> Log.d(TAG, "searching " + query))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .switchMap(query -> comicNetworkService.searchComic(query, null))
                .map(result -> result.getData())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(comics -> {
                    ComicSearchAdapter adapter = new ComicSearchAdapter(getContext(), comics);
                    adapter.setOnItemClickListener(((itemView, position) ->
                            getOnItemSelectedListener().OnItemSelected(adapter.getItem(position), itemView)));
                    mRecyclerView.setAdapter(adapter);
                    updateGridLayout();
                }, error -> {
                    Log.e(TAG, error.getMessage());
                });
        searchView.setOnSearchClickListener(view->{
            mRecyclerView.setAdapter(null);
            mRecyclerView.removeOnScrollListener(endlessRecyclerViewOnScrollListener);
            updateGridLayout();
            mSwipeRefreshLayout.setEnabled(false);
        });
    }

    @Override
    protected Uri getContentUri() {
        return ComicContract.ComicEntry.CONTENT_URI;
    }

    @Override
    protected void onCursorLoaded(Cursor data) {
        getAdapter().changeCursor(data);
        if (data == null || data.getCount() == 0) {
            refreshComic();
        }
    }

    private void refreshComic() {
        mSwipeRefreshLayout.setRefreshing(true);
        comicService.refreshComic();
    }

    @Override
    protected void onRefreshAction() {
        refreshComic();
    }

    @Override
    protected void onComicGridInitFinished() {
        endlessRecyclerViewOnScrollListener = new EndlessRecyclerViewOnScrollListener(getGridLayoutManager()) {
            @Override
            public void onLoadMore() {
                mSwipeRefreshLayout.setRefreshing(true);
                comicService.loadMoreComic();
            }
        };
        mRecyclerView.addOnScrollListener(endlessRecyclerViewOnScrollListener);
    }
}
