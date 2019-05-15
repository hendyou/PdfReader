package com.recruit.pdfreader.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

public class PermissionUtil {
    public static final int REQUEST_CODE_PERMISSIONS = 0x111;

    public static boolean checkPermission (Activity activity, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //验证是否许可权限
            for (String str : permissions) {
                if (activity.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    activity.requestPermissions(permissions, REQUEST_CODE_PERMISSIONS);
                    return false;
                }
            }
        }

        return true;
    }

}
