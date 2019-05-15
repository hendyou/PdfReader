package com.recruit.pdfreader.common;

import android.app.Application;

import com.recruit.pdfreader.utils.AppInfo;
import com.recruit.pdfreader.utils.CrashHandler;

public class PdfReaderApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Crash Log
        if (AppInfo.isApkInDebug(this) == false) {
            CrashHandler handler = CrashHandler.getInstance();
            handler.init(getApplicationContext());
        }
    }
}
