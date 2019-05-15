package com.recruit.pdfreader.ui.activity;

import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.recruit.pdfreader.R;
import com.recruit.pdfreader.common.Constant;
import com.recruit.pdfreader.utils.ViewUtil;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;

public class PdfShelfActivity extends BaseActivity {
    @BindView(R.id.gridView)
    GridView gridView;

    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pdf_shelf);

        initPdfShelf();


    }

    @Override
    protected void initToolBar() {
        super.initToolBar();

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.mipmap.bookshelf_caption));
    }

    private void initPdfShelf() {

        ViewUtil.getViewMeasuredSize(gridView, (view, measuredWidth, measuredHeight) -> {

            int width = measuredWidth;
            float cellMinWidth = getResources().getDimension(R.dimen.bookshelf_cell_minwidth);
            int numColums = (int) Math.floor(width / cellMinWidth);
            gridView.setNumColumns(numColums);

            int height = measuredHeight;

            float cellHeight = getResources().getDimension(R.dimen.bookshelf_cell_height);

            int rows = (int) Math.ceil(height / cellHeight);

            gridView.setAdapter(new GridAdaper(numColums * rows));

        });






    }

    private class GridAdaper implements ListAdapter {
        private int count;

        public void setCount(int count) {
            this.count = count;
        }

        public GridAdaper() {
            this(0);
        }

        public GridAdaper(int count) {
            this.count = count;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(PdfShelfActivity.this).inflate(R.layout.pdf_shelf_cell, null);
            }

            TextView titleTextView = convertView.findViewById(R.id.title);
            ImageView thumbnail = convertView.findViewById(R.id.thumbnail);

            if (getItemViewType(position) == 0) {
                final String name = "Book" + (position + 1);
                titleTextView.setText(name);

                thumbnail.setClickable(true);
                thumbnail.setOnClickListener(v -> {
                    Intent intent = new Intent(PdfShelfActivity.this, PdfViewerActivity.class);
                    intent.putExtra(Constant.PDF_NAME, name);
                    startActivity(intent);
                });

                setThumbnail(thumbnail, name);

            } else {
                thumbnail.setVisibility(View.INVISIBLE);
                titleTextView.setVisibility(View.INVISIBLE);
            }



            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            String[] files = new String[0];
            try {
                files = getAssets().list("thumbnail");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (files != null && position < files.length) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }


    private void setThumbnail(ImageView thumbnail, String name) {
        try {
            InputStream inputStream = getAssets().open("thumbnail/" + name + ".JPG");
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            thumbnail.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
