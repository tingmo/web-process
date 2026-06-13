package com.example.webmultiprocess;

import android.content.Context;

import com.example.webmultiprocess.util.ProcessUtils;

public final class ProcessModeChooser {
    private ProcessModeChooser() {
    }

    public static boolean shouldUseMultiProcess(Context context) {
        return !ProcessUtils.isLowRamDevice(context);
    }

    public static String localModeReason(Context context) {
        if (ProcessUtils.isLowRamDevice(context)) {
            return "low_ram_device";
        }
        return "manual";
    }
}
