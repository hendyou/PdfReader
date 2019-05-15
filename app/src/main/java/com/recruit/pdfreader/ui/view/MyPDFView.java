package com.recruit.pdfreader.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;

import com.github.barteksc.pdfviewer.PDFView;
import com.recruit.pdfreader.common.Constant;

public class MyPDFView extends PDFView {

    /**
     * Construct the initial view
     *
     * @param context
     * @param set
     */
    public MyPDFView(Context context, AttributeSet set) {
        super(context, set);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
