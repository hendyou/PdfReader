package com.recruit.pdfreader.utils;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import androidx.core.graphics.drawable.DrawableCompat;

public class DrawableTintUtil {
    /**
     * Drawable 颜色转化类
     *
     * @param drawable 源Drawable
     * @param ColorStateList
     * @return 改变颜色后的Drawable
     */
    public static Drawable tintListDrawable(Drawable drawable, ColorStateList colors) {
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }
}
