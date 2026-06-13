package com.example.webmultiprocess.jsapi.handlers;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.webmultiprocess.jsapi.JsonUtils;
import com.example.webmultiprocess.jsapi.JsApiContext;
import com.example.webmultiprocess.jsapi.JsApiHandler;
import com.example.webmultiprocess.jsapi.JsApiResult;

import org.json.JSONObject;

public class ToastHandler implements JsApiHandler {
    @Override
    public String name() {
        return "ui.toast";
    }

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public String description() {
        return "Show a short native Toast.";
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
        return JsonUtils.object(
                "type", "object",
                "required", JsonUtils.array("message"),
                "properties", JsonUtils.object(
                        "message", JsonUtils.object("type", "string"),
                        "long", JsonUtils.object("type", "boolean")));
    }

    @Override
    public JSONObject resultSchema() {
        return JsonUtils.object(
                "type", "object",
                "properties", JsonUtils.object("displayed", JsonUtils.object("type", "boolean")));
    }

    @Override
    public JsApiResult handle(final JsApiContext context, JSONObject params) {
        final String message = params.optString("message");
        if (TextUtils.isEmpty(message)) {
            return JsApiResult.error("INVALID_PARAM", "message is required.");
        }
        final int duration = params.optBoolean("long") ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context.getContext(), message, duration).show();
            }
        });
        return JsApiResult.success(JsonUtils.object("displayed", true));
    }
}
