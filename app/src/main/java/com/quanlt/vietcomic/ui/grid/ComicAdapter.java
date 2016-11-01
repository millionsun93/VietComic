package com.quanlt.vietcomic.ui.grid;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.florent37.glidepalette.GlidePalette;
import com.quanlt.vietcomic.R;
import com.quanlt.vietcomic.data.Comic;
import com.quanlt.vietcomic.util.CursorRecyclerViewAdapter;
import com.quanlt.vietcomic.util.OnItemClickListener;

public class ComicAdapter extends CursorRecyclerViewAdapter<ComicItemViewHolder> {
    private final Context context;
    private OnItemClickListener onItemClickListener;

    public ComicAdapter(Context context, Cursor cursor) {
        super(cursor);
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(ComicItemViewHolder viewHolder, Cursor cursor) {
        if (cursor != null) {
            Comic comic = Comic.fromCursor(cursor);
            viewHolder.mComicTitle.setText(comic.getTitle());
            viewHolder.mLatestChapterTitle.setText(comic.getLastestChapter());
            Glide.with(context).load(comic.getThumbnail())
                    .placeholder(new ColorDrawable(context.getResources().getColor(R.color.colorAccent)))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .crossFade()
                    .listener(GlidePalette.with(comic.getThumbnail())
                            .intoCallBack(palette -> {
                                Palette.Swatch swatch = palette.getVibrantSwatch();
                                if (swatch != null) {
                                    viewHolder.mFooterView.setBackgroundColor(swatch.getRgb());
                                    viewHolder.mComicTitle.setTextColor(swatch.getBodyTextColor());
                                    viewHolder.mLatestChapterTitle.setTextColor(swatch.getBodyTextColor());
                                }
                            }))
                    .into(viewHolder.mComicThumbnail);
        }

    }

    @Override
    public ComicItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_comic, parent, false);
        return new ComicItemViewHolder(itemView, onItemClickListener);
    }


    public Comic getItem(int position) {
        Cursor cursor = getCursor();
        if (cursor == null) {
            return null;
        }
        if (position < 0 || position > cursor.getCount()) {
            return null;
        }
        cursor.moveToFirst();
        for (int i = 0; i < position; i++)
            cursor.moveToNext();
        return Comic.fromCursor(cursor);
    }
}
