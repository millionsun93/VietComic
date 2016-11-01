package com.quanlt.vietcomic.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageAspect extends ImageView {
    private static final float ASPECT_RATIO = 1.5f;

    public ImageAspect(Context context) {
        super(context);
    }

    public ImageAspect(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageAspect(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = Math.round(width * ASPECT_RATIO);
        setMeasuredDimension(width, height);
    }
}
