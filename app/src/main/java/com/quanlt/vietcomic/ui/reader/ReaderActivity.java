package com.quanlt.vietcomic.ui.reader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.quanlt.vietcomic.ComicApplication;
import com.quanlt.vietcomic.R;
import com.quanlt.vietcomic.api.ComicNetworkService;
import com.quanlt.vietcomic.data.Chapter;
import com.quanlt.vietcomic.data.Comic;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ReaderActivity extends AppCompatActivity implements ComicPager.OnSwipeOutListener {

    private static final String TAG = ReaderActivity.class.getSimpleName();
    @Inject
    ComicNetworkService networkService;
    @BindView(R.id.pageProgressBar)
    ProgressBar mLoadingProgressBar;
    @BindView(R.id.reloadButton)
    ImageButton mReloadButton;
    public static final String COMIC_ARG = "COMIC_ARG";
    public static final String CHAPTER_TITLE_ARG = "CHAPTER_TITLE_ARG";
    public static final String CHAPTER_ARG = "CHAPTER_ARG";
    private Comic mSelectedComic;
    private Chapter mChapter;
    private String mChapterTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        ButterKnife.bind(this);
        ((ComicApplication) getApplication()).getNetworkComponent().inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener((view -> onBackPressed()));
        mSelectedComic = getIntent().getParcelableExtra(COMIC_ARG);
        mChapterTitle = getIntent().getStringExtra(CHAPTER_TITLE_ARG);
        if (savedInstanceState != null) {
            mChapter = savedInstanceState.getParcelable(CHAPTER_ARG);
        }
        if (mChapter == null) {
            loadChapter();
        } else {
            if (getSupportFragmentManager().findFragmentByTag(ReaderFragment.TAG) == null)
                updateFragment();
            else {
                mLoadingProgressBar.setVisibility(View.GONE);
                mReloadButton.setVisibility(View.GONE);
            }
        }
    }

    private void loadChapter() {
        setTitle(mChapterTitle);
        mReloadButton.setVisibility(View.GONE);
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        networkService.getChapter(mSelectedComic.getId(), mChapterTitle)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(response -> response.getData())
                .subscribe(chapter -> {
                    mChapter = chapter;
                    updateFragment();
                }, error -> {
                    Log.e(TAG, error.getMessage());
                    mReloadButton.setVisibility(View.VISIBLE);
                    mLoadingProgressBar.setVisibility(View.GONE);
                });
    }

    @OnClick(R.id.reloadButton)
    void reload() {
        loadChapter();
    }

    private void updateFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_reader, ReaderFragment.newInstance(mChapter), ReaderFragment.TAG)
                .commit();
        mLoadingProgressBar.setVisibility(View.GONE);
        mReloadButton.setVisibility(View.GONE);
    }

    private void removeFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(ReaderFragment.TAG);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CHAPTER_ARG, mChapter);
    }

    @Override
    public void onSwipeAtLast() {
        switchChapter(getString(R.string.switch_to_next), true);
    }

    @Override
    public void onSwipeAtFirst() {
        switchChapter(getString(R.string.switch_to_prev), false);
    }

    private void switchChapter(String title, boolean next) {
        int idx = mSelectedComic.getChapters().indexOf(mChapter);
        int nextIndex = next ? ++idx : --idx;
        if (nextIndex < 0 || nextIndex >= mSelectedComic.getChapters().size()) {
            return;
        }
        String nextChapter = mSelectedComic.getChapters().get(next ? nextIndex : nextIndex).getTitle();
        AlertDialog switchDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(nextChapter)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    removeFragment();
                    Intent intent = new Intent();
                    intent.putExtra(COMIC_ARG, mSelectedComic);
                    intent.putExtra(CHAPTER_TITLE_ARG, nextChapter);
                    setIntent(intent);
                    mChapter = null;
                    mChapterTitle = nextChapter;
                    loadChapter();
                }).setNegativeButton(getString(R.string.no), (dialog, which) -> {
                    //do nothing
                }).create();
        switchDialog.show();
    }
}
