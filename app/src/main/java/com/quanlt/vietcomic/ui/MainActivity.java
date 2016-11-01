package com.quanlt.vietcomic.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.quanlt.vietcomic.ComicApplication;
import com.quanlt.vietcomic.R;
import com.quanlt.vietcomic.data.Comic;
import com.quanlt.vietcomic.data.ComicService;
import com.quanlt.vietcomic.ui.detail.ComicDetailActivity;
import com.quanlt.vietcomic.ui.detail.ComicDetailFragment;
import com.quanlt.vietcomic.ui.grid.ComicFragment;
import com.quanlt.vietcomic.ui.grid.FavoriteFragment;
import com.quanlt.vietcomic.util.OnItemSelectedListener;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnItemSelectedListener {

    private static final String SELECTED_NAVIGATION_ITEM = "SELECTED_NAVIGATION_ITEM";
    private static final String SELECTED_COMIC = "SELECTED_COMIC";

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;
    @Nullable
    @BindView(R.id.comic_detail_container)
    ScrollView mComicDetailContainer;
    @Nullable
    @BindView(R.id.fab)
    FloatingActionButton mLikeFloatingButton;

    private int mSelectedNavigationItem;
    private boolean isTwoPanel;
    private Comic mSelectedComic;

    @Inject
    ComicService mComicService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((ComicApplication) getApplication()).getNetworkComponent().inject(this);
        if (savedInstanceState == null) {
            mSelectedNavigationItem = 0;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.comic_grid_container, ComicFragment.newInstance())
                    .commit();
        }
        isTwoPanel = mComicDetailContainer != null;
        if (isTwoPanel && mSelectedComic == null) {
            mComicDetailContainer.setVisibility(View.GONE);
        }
        setupToolbar();
        setupNavigationDrawer();
        setupNavigationView();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_NAVIGATION_ITEM, mSelectedNavigationItem);
        outState.putParcelable(SELECTED_COMIC, mSelectedComic);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mSelectedNavigationItem = savedInstanceState.getInt(SELECTED_NAVIGATION_ITEM);
            mSelectedComic = savedInstanceState.getParcelable(SELECTED_COMIC);
            Menu menu = mNavigationView.getMenu();
            menu.getItem(mSelectedNavigationItem).setChecked(true);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
    }

    private void setupNavigationDrawer() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mToolbar.setNavigationIcon(R.drawable.ic_menu);
        mToolbar.setNavigationOnClickListener(view -> mDrawerLayout.openDrawer(GravityCompat.START));
    }

    private void setupNavigationView() {
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.nav_home:
                if (mSelectedNavigationItem != 0) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.comic_grid_container, ComicFragment.newInstance())
                            .commit();
                    mSelectedNavigationItem = 0;
                    updateTitle();
                    hideComicDetailContainer();
                }
                break;
            case R.id.nav_favorite:
                if (mSelectedNavigationItem != 1) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.comic_grid_container, new FavoriteFragment())
                            .commit();
                    mSelectedNavigationItem = 1;
                    updateTitle();
                    hideComicDetailContainer();
                }
                break;
            case R.id.nav_about:
                new LibsBuilder()
                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                        .withActivityTitle(getString(R.string.about))
                        .start(this);
                break;
            default:
                return false;
        }
        mDrawerLayout.closeDrawers();
        return true;
    }

    private void updateTitle() {
        if (mSelectedNavigationItem == 0) {
            setTitle(getString(R.string.home));
        } else {
            setTitle(getString(R.string.favorite));
        }
    }

    @Override
    public void OnItemSelected(Comic comic, View view) {
        if (isTwoPanel) {
            mSelectedComic = comic;
            mComicDetailContainer.setVisibility(View.VISIBLE);
            mSelectedComic = comic;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.comic_detail_container, ComicDetailFragment.newInstance(comic))
                    .commit();
            setupFab();
        } else {
            Intent intent = new Intent(this, ComicDetailActivity.class);
            intent.putExtra(ComicDetailActivity.ARG_COMIC, comic);
            Pair<View, String> p1 = Pair.create(view.findViewById(R.id.tv_comic_title), getString(R.string.title));
            Pair<View, String> p2 = Pair.create(view.findViewById(R.id.iv_comic_thumbnail), getString(R.string.thumbnail));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2);
            if (Build.VERSION.SDK_INT < 16) {
                startActivity(intent);
            } else {
                startActivity(intent, options.toBundle());
            }
        }
    }

    private void setupFab() {
        if (mLikeFloatingButton != null) {
            if (isTwoPanel && mSelectedComic != null) {
                if (mComicService.isFavorite(mSelectedComic)) {
                    mLikeFloatingButton.setImageResource(R.drawable.ic_favorite_white_24dp);
                } else {
                    mLikeFloatingButton.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                }
                mLikeFloatingButton.show();
            } else {
                mLikeFloatingButton.hide();
            }
        }
    }

    @Optional
    @OnClick(R.id.fab)
    void onFabClick() {
        if (mComicService.isFavorite(mSelectedComic)) {
            mComicService.removeFromFavorite(mSelectedComic);
            if (mSelectedNavigationItem == 1) {
                hideComicDetailContainer();
            }
        } else {
            mComicService.addToFavorite(mSelectedComic);
        }
        setupFab();
    }

    private void hideComicDetailContainer() {
        mSelectedComic = null;
        if (isTwoPanel && mComicDetailContainer != null) {
            mComicDetailContainer.setVisibility(View.GONE);
        }
        setupFab();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
