package com.example.webmultiprocess.jsapi.handlers;

import android.content.Context;
import android.text.TextUtils;

import com.example.webmultiprocess.jsapi.JsonUtils;
import com.example.webmultiprocess.jsapi.JsApiContext;
import com.example.webmultiprocess.jsapi.JsApiHandler;
import com.example.webmultiprocess.jsapi.JsApiResult;

import org.json.JSONObject;

public class StorageSetHandler implements JsApiHandler {
    private static final String STORAGE_NAME = "jsapi_storage";

    @Override
    public String name() {
        return "storage.set";
    }

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public String description() {
        return "Persist a string value in main-process SharedPreferences.";
    }

    @Override
    public boolean mainProcessOnly() {
        return true;
    }

    @Override
    public boolean allowLocalFallback() {
        return false;
    }

    @Override
    public JSONObject paramsSchema() {
        return JsonUtils.object(
                "type", "object",
                "required", JsonUtils.array("key", "value"),
                "properties", JsonUtils.object(
                        "key", JsonUtils.object("type", "string"),
                        "value", JsonUtils.object("type", "string")));
    }

    @Override
    public JSONObject resultSchema() {
        return JsonUtils.object(
                "type", "object",
                "properties", JsonUtils.object("saved", JsonUtils.object("type", "boolean")));
    }

    @Override
    public JsApiResult handle(JsApiContext context, JSONObject params) {
        String key = params.optString("key");
        if (TextUtils.isEmpty(key)) {
            return JsApiResult.error("INVALID_PARAM", "key is required.");
        }
        String value = params.optString("value");
        context.getContext()
                .getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(key, value)
                .apply();
        return JsApiResult.success(JsonUtils.object("saved", true, "key", key));
    }
}
