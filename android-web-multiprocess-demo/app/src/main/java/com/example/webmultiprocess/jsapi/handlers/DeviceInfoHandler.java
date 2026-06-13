package com.example.webmultiprocess.jsapi.handlers;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.example.webmultiprocess.jsapi.JsonUtils;
import com.example.webmultiprocess.jsapi.JsApiContext;
import com.example.webmultiprocess.jsapi.JsApiHandler;
import com.example.webmultiprocess.jsapi.JsApiResult;
import com.example.webmultiprocess.util.ProcessUtils;

import org.json.JSONObject;

public class DeviceInfoHandler implements JsApiHandler {
    @Override
    public String name() {
        return "device.getInfo";
    }

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public String description() {
        return "Return app, process, Android SDK, and device model information.";
    }

    @Override
    public boolean mainProcessOnly() {
        return false;
    }

    @Override
    public boolean allowLocalFallback() {
        return true;
    }

    @Override
    public JSONObject paramsSchema() {
        return JsonUtils.object("type", "object", "properties", JsonUtils.object());
    }

    @Override
    public JSONObject resultSchema() {
        return JsonUtils.object(
                "type", "object",
                "properties", JsonUtils.object(
                        "packageName", JsonUtils.object("type", "string"),
                        "versionName", JsonUtils.object("type", "string"),
                        "process", JsonUtils.object("type", "string"),
                        "sdkInt", JsonUtils.object("type", "number")));
    }

    @Override
    public JsApiResult handle(JsApiContext context, JSONObject params) {
        String versionName = "unknown";
        try {
            PackageInfo packageInfo = context.getContext()
                    .getPackageManager()
                    .getPackageInfo(context.getContext().getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        JSONObject data = JsonUtils.object(
                "packageName", context.getContext().getPackageName(),
                "versionName", versionName,
                "process", ProcessUtils.currentProcessName(context.getContext()),
                "isMainProcess", ProcessUtils.isMainProcess(context.getContext()),
                "isWebProcess", ProcessUtils.isWebProcess(context.getContext()),
                "manufacturer", Build.MANUFACTURER,
                "model", Build.MODEL,
                "sdkInt", Build.VERSION.SDK_INT);
        return JsApiResult.success(data);
    }
}
