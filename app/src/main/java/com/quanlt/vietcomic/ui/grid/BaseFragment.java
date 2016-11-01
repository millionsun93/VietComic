package com.quanlt.vietcomic.ui.grid;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.quanlt.vietcomic.R;
import com.quanlt.vietcomic.ui.view.GridItemDecoration;
import com.quanlt.vietcomic.util.OnItemClickListener;
import com.quanlt.vietcomic.util.OnItemSelectedListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final int LOADER_ID = 101;
    @BindView(R.id.view_no_comic)
    RelativeLayout mNoComicView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.comic_grid)
    RecyclerView mRecyclerView;
    private OnItemSelectedListener onItemSelectedListener;
    private ComicAdapter mAdapter;
    private GridLayoutManager gridLayoutManager;

    public BaseFragment() {

    }

    protected abstract Uri getContentUri();

    protected abstract void onCursorLoaded(Cursor data);

    protected abstract void onRefreshAction();

    protected abstract void onComicGridInitFinished();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comic_grid, container, false);
        ButterKnife.bind(this, view);
        initSwipeRefreshLayout();
        initComicsGrid();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateGridLayout();
    }

    public OnItemSelectedListener getOnItemSelectedListener() {
        return onItemSelectedListener;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            onItemSelectedListener = (OnItemSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement " + AdapterView.OnItemSelectedListener.class.getSimpleName());
        }
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    protected void initComicsGrid() {
        mAdapter = new ComicAdapter(getActivity(), null);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new GridItemDecoration(getActivity(), R.dimen.comic_offset));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        int columns = getResources().getInteger(R.integer.movies_columns);
        gridLayoutManager = new GridLayoutManager(getActivity(), columns);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        onComicGridInitFinished();
    }

    protected void updateGridLayout() {
        if (mRecyclerView.getAdapter() == null || mRecyclerView.getAdapter().getItemCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mNoComicView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoComicView.setVisibility(View.GONE);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), getContentUri(), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        onCursorLoaded(data);
        updateGridLayout();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
        updateGridLayout();
    }

    protected void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onRefresh() {
        onRefreshAction();
    }

    @Override
    public void onItemClick(View itemView, int position) {
        onItemSelectedListener.OnItemSelected(mAdapter.getItem(position), itemView);
    }

    public ComicAdapter getAdapter() {
        return mAdapter;
    }

    public GridLayoutManager getGridLayoutManager() {
        return gridLayoutManager;
    }

}
