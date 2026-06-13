package com.example.webmultiprocess.jsapi.handlers;

import android.content.Context;
import android.text.TextUtils;

import com.example.webmultiprocess.jsapi.JsApiContract;
import com.example.webmultiprocess.jsapi.ConfiguredJsApiHandler;
import com.example.webmultiprocess.jsapi.JsonUtils;
import com.example.webmultiprocess.jsapi.JsApiContext;
import com.example.webmultiprocess.jsapi.JsApiResult;

import org.json.JSONObject;

public class StorageGetHandler extends ConfiguredJsApiHandler {
    public StorageGetHandler() {
        super(JsApiContract.STORAGE_GET);
    }

    @Override
    public JSONObject paramsSchema() {
        return JsonUtils.object(
                "type", "object",
                "required", JsonUtils.array(JsApiContract.Param.KEY),
                "properties", JsonUtils.object(JsApiContract.Param.KEY, JsonUtils.object("type", "string")));
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
        String key = params.optString(JsApiContract.Param.KEY);
        if (TextUtils.isEmpty(key)) {
            return JsApiResult.error(JsApiContract.Code.INVALID_PARAM, "key is required.");
        }
        String value = context.getContext()
                .getSharedPreferences(JsApiContract.DemoStorage.PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(key, null);
        return JsApiResult.success(JsonUtils.object(
                "found", value != null,
                JsApiContract.Param.KEY, key,
                JsApiContract.Param.VALUE, value == null ? "" : value));
    }
}
