package com.quanlt.vietcomic.ui.reader;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ComicPager extends ViewPager {
    private float mStartX;
    private OnSwipeOutListener mListener;

    public ComicPager(Context context) {
        super(context);
    }

    public ComicPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setListener(OnSwipeOutListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
            mStartX = ev.getX();
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            
            if (ev.getX() > mStartX && getCurrentItem() == 0) {
                mListener.onSwipeAtFirst();
            } else if (ev.getX() < mStartX && getCurrentItem() == (getAdapter().getCount() - 1)) {
                mListener.onSwipeAtLast();
            }
        }
        return super.onTouchEvent(ev);
    }


    public interface OnSwipeOutListener {
        void onSwipeAtLast();

        void onSwipeAtFirst();
    }
}
