package com.quanlt.vietcomic.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.quanlt.vietcomic.ComicApplication;
import com.quanlt.vietcomic.R;
import com.quanlt.vietcomic.api.ComicNetworkService;
import com.quanlt.vietcomic.data.Chapter;
import com.quanlt.vietcomic.data.Comic;
import com.quanlt.vietcomic.ui.reader.ReaderActivity;
import com.quanlt.vietcomic.ui.view.ImageAspect;

import java.text.SimpleDateFormat;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.gujun.android.taggroup.TagGroup;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ComicDetailFragment extends Fragment {

    private static final String ARG_COMIC = "Arg Comic";
    private static final String TAG = ComicDetailFragment.class.getSimpleName();
    @BindView(R.id.iv_comic_thumbnail)
    ImageAspect mComicThumbnail;
    @BindView(R.id.tv_title)
    TextView mComicTitle;
    @BindView(R.id.tg_author)
    TagGroup mAuthorTagGroup;
    @BindView(R.id.tv_update_time)
    TextView mUpdateTime;
    @BindView(R.id.tg_categories)
    TagGroup mCategoriesTagGroup;
    @BindView(R.id.tv_description)
    TextView mDescriptionTextView;
    @BindView(R.id.tv_viewer_count)
    TextView mViewerCount;
    @BindView(R.id.cv_description)
    CardView mDescriptionCardView;
    @BindView(R.id.rv_chapters)
    RecyclerView mChapterRecyclerView;
    @BindView(R.id.cv_chapters)
    CardView mChapterCardView;
    private Comic comic;
    private SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy hh:mm:ss");
    @Inject
    ComicNetworkService mComicNetworkService;
    private ChapterAdapter mChapterAdapter;

    public static ComicDetailFragment newInstance(Comic comic) {

        Bundle args = new Bundle();

        ComicDetailFragment fragment = new ComicDetailFragment();
        args.putParcelable(ARG_COMIC, comic);
        fragment.setArguments(args);
        return fragment;
    }

    public ComicDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        comic = getArguments().getParcelable(ARG_COMIC);
        ((ComicApplication) getActivity().getApplication()).getNetworkComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comic_detail, container, false);
        ButterKnife.bind(this, view);
        initView();
        initChaptersList();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mChapterAdapter.getItemCount() == 0) {
            loadChapters();
        }
        updateChapterCard();
    }

    private void loadChapters() {
        mComicNetworkService.getComicDetail(comic.getId())
                .subscribeOn(Schedulers.io())
                .map(response -> response.getData())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    comic = result;
                    mChapterAdapter.setChapters(result.getChapters());
                    updateChapterCard();
                }, error -> {
                    Log.e(TAG, error.getMessage());
                    updateChapterCard();
                });
    }

    private void updateChapterCard() {
        if (mChapterAdapter.getItemCount() == 0) {
            mChapterCardView.setVisibility(View.GONE);
        } else {
            mChapterCardView.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        mAuthorTagGroup.setTags(comic.getAuthors());
        mCategoriesTagGroup.setTags(comic.getCategories());
        mComicTitle.setText(comic.getTitle());
        mViewerCount.setText(getString(R.string.view_count, comic.getViewers()));
        mUpdateTime.setText(getString(R.string.update_on, format.format(comic.getUpdateTime())));
        Glide.with(this).load(comic.getThumbnail())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mComicThumbnail);
        if (TextUtils.isEmpty(comic.getDescription())) {
            mDescriptionCardView.setVisibility(View.GONE);
        } else {
            mDescriptionCardView.setVisibility(View.VISIBLE);
            mDescriptionTextView.setText(comic.getDescription());
        }
    }

    private void initChaptersList() {
        mChapterAdapter = new ChapterAdapter();
        mChapterAdapter.setOnItemClickListener((view, position) -> onChapterClicked(position));
        mChapterRecyclerView.setAdapter(mChapterAdapter);
        mChapterRecyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mChapterRecyclerView.setLayoutManager(layoutManager);
    }

    private void onChapterClicked(int position) {
        Chapter chapter = mChapterAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), ReaderActivity.class);
        intent.putExtra(ReaderActivity.CHAPTER_TITLE_ARG, chapter.getTitle());
        intent.putExtra(ReaderActivity.COMIC_ARG, comic);
        startActivity(intent);
    }
}
