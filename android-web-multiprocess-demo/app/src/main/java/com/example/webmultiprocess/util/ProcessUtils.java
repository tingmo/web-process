package com.example.webmultiprocess.util;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;

import java.util.List;

public final class ProcessUtils {
    private ProcessUtils() {
    }

    public static String currentProcessName(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Application.getProcessName();
        }
        int pid = Process.myPid();
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return "";
        }
        List<ActivityManager.RunningAppProcessInfo> processes =
                activityManager.getRunningAppProcesses();
        if (processes == null) {
            return "";
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : processes) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return "";
    }

    public static boolean isMainProcess(Context context) {
        String processName = currentProcessName(context);
        return TextUtils.equals(context.getPackageName(), processName);
    }

    public static boolean isWebProcess(Context context) {
        return currentProcessName(context).endsWith(":web");
    }

    public static boolean isLowRamDevice(Context context) {
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager != null && activityManager.isLowRamDevice();
    }
}
