package com.quanlt.vietcomic.ui.grid;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.florent37.glidepalette.GlidePalette;
import com.quanlt.vietcomic.R;
import com.quanlt.vietcomic.data.Comic;
import com.quanlt.vietcomic.util.OnItemClickListener;

import java.util.List;

public class ComicSearchAdapter extends RecyclerView.Adapter<ComicItemViewHolder> {
    private List<Comic> comicList;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public ComicSearchAdapter(Context context, List<Comic> comicList) {
        this.comicList = comicList;
        this.context = context;
    }

    @Override
    public ComicItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_comic, parent, false);
        return new ComicItemViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(ComicItemViewHolder holder, int position) {
        Comic comic = comicList.get(position);
        holder.mComicTitle.setText(comic.getTitle());
        holder.mLatestChapterTitle.setText(comic.getLastestChapter());
        Glide.with(context).load(comic.getThumbnail())
                .placeholder(new ColorDrawable(context.getResources().getColor(R.color.colorAccent)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .crossFade()
                .listener(GlidePalette.with(comic.getThumbnail())
                        .intoCallBack(palette -> {
                            Palette.Swatch swatch = palette.getVibrantSwatch();
                            if (swatch != null) {
                                holder.mFooterView.setBackgroundColor(swatch.getRgb());
                                holder.mComicTitle.setTextColor(swatch.getBodyTextColor());
                                holder.mLatestChapterTitle.setTextColor(swatch.getBodyTextColor());
                            }
                        }))
                .into(holder.mComicThumbnail);
    }

    @Override
    public int getItemCount() {
        return comicList.size();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public Comic getItem(int position) {
        return comicList.get(position);
    }
}
