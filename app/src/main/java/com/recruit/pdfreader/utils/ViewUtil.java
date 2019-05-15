package com.recruit.pdfreader.utils;

import android.view.View;
import android.view.ViewTreeObserver;

public class ViewUtil {

    public interface OnViewSizeMeausedListener {
        public void onSizeMeaused(View view, int measuredWidth, int measuredHeight);
    }

    public static void getViewMeasuredSize(View view, OnViewSizeMeausedListener onViewSizeMeausedListener) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (onViewSizeMeausedListener != null) {
                    onViewSizeMeausedListener.onSizeMeaused(view, view.getMeasuredWidth(), view.getMeasuredHeight());
                }
            }
        });
    }
}
