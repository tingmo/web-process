package com.example.webmultiprocess.jsapi.handlers;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.webmultiprocess.jsapi.ApiConfigs;
import com.example.webmultiprocess.jsapi.BridgeCodes;
import com.example.webmultiprocess.jsapi.ConfiguredJsApiHandler;
import com.example.webmultiprocess.jsapi.DemoParams;
import com.example.webmultiprocess.jsapi.JsonUtils;
import com.example.webmultiprocess.jsapi.JsApiContext;
import com.example.webmultiprocess.jsapi.JsApiResult;

import org.json.JSONObject;

public class ToastHandler extends ConfiguredJsApiHandler {
    public ToastHandler() {
        super(ApiConfigs.UI_TOAST);
    }

    @Override
    public JSONObject paramsSchema() {
        return JsonUtils.object(
                "type", "object",
                "required", JsonUtils.array(DemoParams.MESSAGE),
                "properties", JsonUtils.object(
                        DemoParams.MESSAGE, JsonUtils.object("type", "string"),
                        DemoParams.LONG, JsonUtils.object("type", "boolean")));
    }

    @Override
    public JSONObject resultSchema() {
        return JsonUtils.object(
                "type", "object",
                "properties", JsonUtils.object("displayed", JsonUtils.object("type", "boolean")));
    }

    @Override
    public JsApiResult handle(final JsApiContext context, JSONObject params) {
        final String message = params.optString(DemoParams.MESSAGE);
        if (TextUtils.isEmpty(message)) {
            return JsApiResult.error(BridgeCodes.INVALID_PARAM, "message is required.");
        }
        final int duration = params.optBoolean(DemoParams.LONG) ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context.getContext(), message, duration).show();
            }
        });
        return JsApiResult.success(JsonUtils.object("displayed", true));
    }
}
