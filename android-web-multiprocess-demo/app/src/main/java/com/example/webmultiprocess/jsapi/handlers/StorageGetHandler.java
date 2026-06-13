package com.example.webmultiprocess.jsapi.handlers;

import android.content.Context;
import android.text.TextUtils;

import com.example.webmultiprocess.jsapi.JsonUtils;
import com.example.webmultiprocess.jsapi.JsApiContext;
import com.example.webmultiprocess.jsapi.JsApiHandler;
import com.example.webmultiprocess.jsapi.JsApiResult;

import org.json.JSONObject;

public class StorageGetHandler implements JsApiHandler {
    private static final String STORAGE_NAME = "jsapi_storage";

    @Override
    public String name() {
        return "storage.get";
    }

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public String description() {
        return "Read a string value from main-process SharedPreferences.";
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
                "required", JsonUtils.array("key"),
                "properties", JsonUtils.object("key", JsonUtils.object("type", "string")));
    }

    @Override
    public JSONObject resultSchema() {
        return JsonUtils.object(
                "type", "object",
                "properties", JsonUtils.object(
                        "found", JsonUtils.object("type", "boolean"),
                        "value", JsonUtils.object("type", "string")));
    }

    @Override
    public JsApiResult handle(JsApiContext context, JSONObject params) {
        String key = params.optString("key");
        if (TextUtils.isEmpty(key)) {
            return JsApiResult.error("INVALID_PARAM", "key is required.");
        }
        String value = context.getContext()
                .getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE)
                .getString(key, null);
        return JsApiResult.success(JsonUtils.object(
                "found", value != null,
                "key", key,
                "value", value == null ? "" : value));
    }
}
