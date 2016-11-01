package com.quanlt.vietcomic.ui.reader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.quanlt.vietcomic.R;
import com.quanlt.vietcomic.data.Chapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ReaderFragment extends Fragment {
    private static final String ARG_CHAPTER = "ARG_CHAPTER";
    public static final String TAG = ReaderFragment.class.getSimpleName();
    @BindView(R.id.pager)
    ComicPager mPager;

    private SeekBar mProgressSeekbar;
    private TextView mPageStatus;
    private View mNavigationLayout;
    private Chapter mChapter;
    private boolean mIsFullScreen;
    private GestureDetector mGestureDetector;
    private ComicPager.OnSwipeOutListener mSwipeOutListener;

    public static ReaderFragment newInstance(Chapter mChapter) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_CHAPTER, mChapter);
        ReaderFragment fragment = new ReaderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChapter = getArguments().getParcelable(ARG_CHAPTER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reader, container, false);
        ButterKnife.bind(this, view);
        mProgressSeekbar = (SeekBar) getActivity().findViewById(R.id.sb_page_progress);
        mPageStatus = (TextView) getActivity().findViewById(R.id.tv_page_status);
        mNavigationLayout = getActivity().findViewById(R.id.ll_navigation_control);
        mProgressSeekbar.setMax(mChapter.getUrls().size());
        mProgressSeekbar.setProgress(0);
        mProgressSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
                mPager.setCurrentItem(position);
                updateStatus();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mPager.setAdapter(new ReaderPagerAdapter());
        mPager.setListener(mSwipeOutListener);
        mPager.setOnTouchListener((view1, motionEvent) -> mGestureDetector.onTouchEvent(motionEvent));
        mGestureDetector = new GestureDetector(getActivity(), new MyTouchListener());
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateStatus();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(()-> setFullScreen(true),300);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ComicPager.OnSwipeOutListener) {
            mSwipeOutListener = (ComicPager.OnSwipeOutListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnSwipeOutListener");
        }
    }


    @Override
    public void onDetach() {
        mSwipeOutListener = null;
        super.onDetach();
    }

    private void updateStatus() {
        if (!isAdded())
            return;
        mPageStatus.setText(getString(R.string.page_status, new String[]{(mPager.getCurrentItem() + 1) + "", mProgressSeekbar.getMax() + ""}));
        mProgressSeekbar.setProgress(mPager.getCurrentItem());
    }

    private void setFullScreen(boolean isFullscreen) {
        this.mIsFullScreen = isFullscreen;
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (mIsFullScreen) {
            if (actionBar != null)
                actionBar.hide();
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;
            mPager.setSystemUiVisibility(flags);
            mNavigationLayout.setVisibility(View.INVISIBLE);
        } else {
            if (actionBar != null)
                actionBar.show();
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            mPageStatus.setSystemUiVisibility(flags);
            mNavigationLayout.setVisibility(View.VISIBLE);
        }
    }

    private class MyTouchListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (!mIsFullScreen) {
                setFullScreen(true);
            } else {
                setFullScreen(false);
            }
            return true;
        }
    }

    private class ReaderPagerAdapter extends PagerAdapter {
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


        @Override
        public int getCount() {
            return mChapter.getUrls().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View view = inflater.inflate(R.layout.view_item_page, container, false);
            PhotoView pageImageView = (PhotoView) view.findViewById(R.id.iv_page);
            container.addView(view);
            PhotoViewAttacher attacher = new PhotoViewAttacher(pageImageView);
            attacher.setOnViewTapListener((view1, x, y) -> {
                if (!mIsFullScreen) {
                    setFullScreen(true);
                } else {
                    setFullScreen(false);
                }
            });
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.pb_progress);
            ImageButton reloadButton = (ImageButton) view.findViewById(R.id.ib_reload_button);
            reloadButton.setOnClickListener(reload -> loadImage(position, pageImageView, progressBar, reloadButton));
            loadImage(position, pageImageView, progressBar, reloadButton);
            return view;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View layout = (View) object;
            container.removeView(layout);
            PhotoView iv = (PhotoView) layout.findViewById(R.id.iv_page);
            Glide.clear(iv);
        }

        private void loadImage(int position, PhotoView pageImageView, View progressBar, View reloadButton) {
            Glide.with(getActivity()).load(mChapter.getUrls().get(position))
                    .crossFade()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new SimpleTarget<GlideDrawable>(SimpleTarget.SIZE_ORIGINAL, SimpleTarget.SIZE_ORIGINAL) {
                        @Override
                        public void onLoadStarted(Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                            progressBar.setVisibility(View.VISIBLE);
                            pageImageView.setVisibility(View.GONE);
                            reloadButton.setVisibility(View.GONE);
                        }


                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            pageImageView.setImageDrawable(resource);
                            progressBar.setVisibility(View.INVISIBLE);
                            pageImageView.setVisibility(View.VISIBLE);
                            reloadButton.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            progressBar.setVisibility(View.INVISIBLE);
                            pageImageView.setVisibility(View.INVISIBLE);
                            reloadButton.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

}
