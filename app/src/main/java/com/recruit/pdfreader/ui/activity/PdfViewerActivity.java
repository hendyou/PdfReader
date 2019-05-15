package com.recruit.pdfreader.ui.activity;

import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.recruit.pdfreader.R;
import com.recruit.pdfreader.common.Constant;
import com.recruit.pdfreader.utils.DrawableTintUtil;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import butterknife.BindView;

public class PdfViewerActivity extends BaseActivity {
    private static final String PDF_PAGE = "pdf_page";
    @BindView(R.id.pdfView) PDFView pdfView;
    @BindView(R.id.seekbar) AppCompatSeekBar seekBar;
    @BindView(R.id.seekbarText) TextView seekbarText;

    String pdfName;
    MediaPlayer mediaPlayer;
    Timer timer;
    Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pdf_viewer);

        initData();

        initPdfView();
        
        initSeekBar();

    }


    private void initSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbarText.setText(progress + 1 + "");

                if (seekBar.getProgress() != pdfView.getCurrentPage()) {
                    jumpToPage(seekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                ValueAnimator animator = ValueAnimator.ofFloat(0, 1.0f);
                animator.setDuration(200);
                animator.addUpdateListener(animation -> seekbarText.setAlpha((Float) animation.getAnimatedValue()));
                animator.start();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


                ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0);
                animator.setDuration(500);
                animator.addUpdateListener((ValueAnimator animation) -> seekbarText.setAlpha((Float) animation.getAnimatedValue()));


                animator.start();

            }
        });


    }

    private void jumpToPage(int page) {
        pdfView.jumpTo(page, false);
        float positionOffset = page * 1.0f / (pdfView.getPageCount() - 1);
        pdfView.setPositionOffset(positionOffset);
    }

    private void initData() {
        pdfName = getIntent().getStringExtra(Constant.PDF_NAME);
        toolbarTitle.setText(pdfName);
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pdf_viewer_menu, menu);
        DrawableTintUtil.tintListDrawable(menu.getItem(0).getIcon(), getResources().getColorStateList(android.R.color.white));
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_play:
                if (item.getTitle().equals(getString(R.string.play))) {


                    startAutoPlay();
                } else {

                    stopAutoPlay();
                }

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startAutoPlay() {
        MenuItem item = menu.getItem(0);
        Drawable drawable = getResources().getDrawable(R.mipmap.icon_pause);
        item.setIcon(DrawableTintUtil.tintListDrawable(drawable, getResources().getColorStateList(android.R.color.white)));
        item.setTitle(R.string.pause);

        if (pdfView.getCurrentPage() == pdfView.getPageCount() - 1) {
            jumpToPage(0);
        }

        timer = new Timer();
        final Handler autoPlayHandler = new Handler((Message msg) -> {
                int currentPage = pdfView.getCurrentPage();
                if (currentPage < pdfView.getPageCount() - 1) {
                    jumpToPage(currentPage + 1);
                }
                if (pdfView.getCurrentPage() == pdfView.getPageCount() - 1) {
                    stopAutoPlay();
                }
                return false;
            });
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                autoPlayHandler.sendEmptyMessage(0);
            }
        }, 5000, 5000);
    }


    private void stopAutoPlay() {
        MenuItem item = menu.getItem(0);
        Drawable drawable = getResources().getDrawable(R.mipmap.icon_play);
        item.setIcon(DrawableTintUtil.tintListDrawable(drawable, getResources().getColorStateList(android.R.color.white)));
        item.setTitle(R.string.play);

        if (timer != null) {
            timer.cancel();
        }
    }

    private void initPdfView() {
        String path = "pdf/" + pdfName + ".pdf";

        pdfView.setMidZoom(2.0f);
        pdfView.setMaxZoom(2.0f);

        PDFView.Configurator pdfConfigurator = pdfView.fromAsset(path)
                .enableSwipe(true)
                .enableDoubletap(true)
                .swipeHorizontal(true)
                .pageSnap(true)
                .autoSpacing(true)
                .pageFling(true)
                .pageFitPolicy(FitPolicy.BOTH)
                .onPageChange((page, pageCount) -> {
                    toolbar.setTitle(page + 1 + "/" + pageCount);
                    if (pageCount -1 != seekBar.getMax()) {
                        seekBar.setMax(pageCount - 1);
                    }
                    if (seekBar.getProgress() != page) {
                        seekBar.setProgress(page);

                    }

                    int resId = getResources().getIdentifier("page" + (page + 1), "raw", getPackageName());
                    if (resId != 0) {
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                        }
                        mediaPlayer = MediaPlayer.create(PdfViewerActivity.this, resId);
                        mediaPlayer.start();
                    }

                });

        pdfConfigurator.load();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                jumpToPage(pdfView.getCurrentPage());
            }
        }, 1000);
    }
}
