package com.example.webmultiprocess.jsapi.handlers;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.example.webmultiprocess.jsapi.JsApiContract;
import com.example.webmultiprocess.jsapi.ConfiguredJsApiHandler;
import com.example.webmultiprocess.jsapi.JsonUtils;
import com.example.webmultiprocess.jsapi.JsApiContext;
import com.example.webmultiprocess.jsapi.JsApiResult;
import com.example.webmultiprocess.util.ProcessUtils;

import org.json.JSONObject;

public class DeviceInfoHandler extends ConfiguredJsApiHandler {
    public DeviceInfoHandler() {
        super(JsApiContract.DEVICE_GET_INFO);
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
