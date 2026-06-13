package com.example.webmultiprocess.jsapi.handlers;

import android.content.Context;
import android.text.TextUtils;

import com.example.webmultiprocess.jsapi.JsApiContract;
import com.example.webmultiprocess.jsapi.ConfiguredJsApiHandler;
import com.example.webmultiprocess.jsapi.JsonUtils;
import com.example.webmultiprocess.jsapi.JsApiContext;
import com.example.webmultiprocess.jsapi.JsApiResult;

import org.json.JSONObject;

public class StorageSetHandler extends ConfiguredJsApiHandler {
    public StorageSetHandler() {
        super(JsApiContract.STORAGE_SET);
    }

    @Override
    public JSONObject paramsSchema() {
        return JsonUtils.object(
                "type", "object",
                "required", JsonUtils.array(JsApiContract.Param.KEY, JsApiContract.Param.VALUE),
                "properties", JsonUtils.object(
                        JsApiContract.Param.KEY, JsonUtils.object("type", "string"),
                        JsApiContract.Param.VALUE, JsonUtils.object("type", "string")));
    }

    @Override
    public JSONObject resultSchema() {
        return JsonUtils.object(
                "type", "object",
                "properties", JsonUtils.object("saved", JsonUtils.object("type", "boolean")));
    }

    @Override
    public JsApiResult handle(JsApiContext context, JSONObject params) {
        String key = params.optString(JsApiContract.Param.KEY);
        if (TextUtils.isEmpty(key)) {
            return JsApiResult.error(JsApiContract.Code.INVALID_PARAM, "key is required.");
        }
        String value = params.optString(JsApiContract.Param.VALUE);
        context.getContext()
                .getSharedPreferences(JsApiContract.DemoStorage.PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(key, value)
                .apply();
        return JsApiResult.success(JsonUtils.object("saved", true, JsApiContract.Param.KEY, key));
    }
}
