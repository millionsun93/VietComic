package com.quanlt.vietcomic.ui.detail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quanlt.vietcomic.R;
import com.quanlt.vietcomic.data.Chapter;
import com.quanlt.vietcomic.util.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterViewHolder> {
    private List<Chapter> mChapters;
    private OnItemClickListener onItemClickListener;

    public ChapterAdapter() {
        mChapters = new ArrayList<>();
    }

    public void setChapters(List<Chapter> mChapters) {
        this.mChapters = mChapters;
        notifyItemRangeInserted(0, mChapters.size());
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public List<Chapter> getChapters() {
        return mChapters;
    }

    @Override
    public ChapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_chapter, parent, false);
        return new ChapterViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(ChapterViewHolder holder, int position) {
        holder.mContent.setText(mChapters.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        if (mChapters == null) return 0;
        return mChapters.size();
    }

    public Chapter getItem(int position) {
        return mChapters.get(position);
    }
}
