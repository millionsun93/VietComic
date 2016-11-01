package com.quanlt.vietcomic.ui.detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.quanlt.vietcomic.util.OnItemClickListener;

public class ChapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView mContent;
    private OnItemClickListener onItemClickListener;

    public ChapterViewHolder(View itemView, OnItemClickListener onItemClickListener) {
        super(itemView);
        mContent = (TextView) itemView;
        this.onItemClickListener = onItemClickListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
