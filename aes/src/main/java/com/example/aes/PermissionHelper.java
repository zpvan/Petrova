package com.example.aes;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.HashMap;

public class PermissionHelper {

    private static int          mRequestCode;
    private static HashMap<Integer, Runnable> mRunnableOnPermissionGranted = new HashMap<>();
    private static HashMap<Integer, Runnable> mRunnableOnPermissionDenied = new HashMap<>();

    public static void runOnPermissionGranted(Activity activity,
                                              Runnable actionOnGranted,
                                              Runnable actionOnDenied,
                                              String... permissions) {
        if (isPermissionGranted(activity, permissions)) {
            new Handler(Looper.getMainLooper()).post(actionOnGranted);
        } else {
            mRunnableOnPermissionGranted.put(mRequestCode, actionOnGranted);
            mRunnableOnPermissionDenied.put(mRequestCode, actionOnDenied);
            ActivityCompat.requestPermissions(activity, permissions, mRequestCode);
            mRequestCode++;
        }
    }

    private static boolean isPermissionGranted(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mRunnableOnPermissionGranted.containsKey(requestCode)) {
            // 这里规定全部权限都通过才算通过
            boolean grant = true;
            // 在A申请权限，然后马上跳转到B，则grantResults.length=0
            if (grantResults.length == 0) grant = false;
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    grant = false;
                }
            }
            if (grant) {
                new Handler(Looper.getMainLooper()).post(mRunnableOnPermissionGranted.get(requestCode));
            } else {
                new Handler(Looper.getMainLooper()).post(mRunnableOnPermissionDenied.get(requestCode));
            }
            mRunnableOnPermissionGranted.remove(mRequestCode);
            mRunnableOnPermissionDenied.remove(mRequestCode);
        }
    }
}
