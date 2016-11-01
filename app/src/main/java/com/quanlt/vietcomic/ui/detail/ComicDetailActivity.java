package com.quanlt.vietcomic.ui.detail;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.quanlt.vietcomic.ComicApplication;
import com.quanlt.vietcomic.R;
import com.quanlt.vietcomic.data.Comic;
import com.quanlt.vietcomic.data.ComicService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ComicDetailActivity extends AppCompatActivity {
    public static final String ARG_COMIC = "Comic";
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.iv_backdrop)
    ImageView mBackdropImageView;
    @BindView(R.id.fab)
    FloatingActionButton mFavoriteButton;
    @Inject
    ComicService mComicService;
    private Comic comic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_detail);
        ButterKnife.bind(this);
        ((ComicApplication) getApplication()).getNetworkComponent().inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
        comic = getIntent().getParcelableExtra(ARG_COMIC);
        mCollapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        mCollapsingToolbar.setTitle(comic.getTitle());
        setTitle("");
        Glide.with(this).load(comic.getThumbnail())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(mBackdropImageView);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, ComicDetailFragment.newInstance(getIntent().getParcelableExtra(ARG_COMIC)))
                    .commit();
        }
        updateFab();
    }

    @OnClick(R.id.fab)
    void onClick() {
        if (mComicService.isFavorite(comic)) {

            mComicService.removeFromFavorite(comic);
        } else {
            mComicService.addToFavorite(comic);
        }
        updateFab();
    }

    private void updateFab() {
        if (mComicService.isFavorite(comic)) {
            mFavoriteButton.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else {
            mFavoriteButton.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }
    }

}
