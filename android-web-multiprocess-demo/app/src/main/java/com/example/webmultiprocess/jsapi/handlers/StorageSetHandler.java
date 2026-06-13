package com.example.webmultiprocess.jsapi.handlers;

import android.content.Context;
import android.text.TextUtils;

import com.example.webmultiprocess.jsapi.ApiConfigs;
import com.example.webmultiprocess.jsapi.BridgeCodes;
import com.example.webmultiprocess.jsapi.ConfiguredJsApiHandler;
import com.example.webmultiprocess.jsapi.DemoParams;
import com.example.webmultiprocess.jsapi.DemoStorageConfig;
import com.example.webmultiprocess.jsapi.JsonUtils;
import com.example.webmultiprocess.jsapi.JsApiContext;
import com.example.webmultiprocess.jsapi.JsApiResult;

import org.json.JSONObject;

public class StorageSetHandler extends ConfiguredJsApiHandler {
    public StorageSetHandler() {
        super(ApiConfigs.STORAGE_SET);
    }

    @Override
    public JSONObject paramsSchema() {
        return JsonUtils.object(
                "type", "object",
                "required", JsonUtils.array(DemoParams.KEY, DemoParams.VALUE),
                "properties", JsonUtils.object(
                        DemoParams.KEY, JsonUtils.object("type", "string"),
                        DemoParams.VALUE, JsonUtils.object("type", "string")));
    }

    @Override
    public JSONObject resultSchema() {
        return JsonUtils.object(
                "type", "object",
                "properties", JsonUtils.object("saved", JsonUtils.object("type", "boolean")));
    }

    @Override
    public JsApiResult handle(JsApiContext context, JSONObject params) {
        String key = params.optString(DemoParams.KEY);
        if (TextUtils.isEmpty(key)) {
            return JsApiResult.error(BridgeCodes.INVALID_PARAM, "key is required.");
        }
        String value = params.optString(DemoParams.VALUE);
        context.getContext()
                .getSharedPreferences(DemoStorageConfig.PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(key, value)
                .apply();
        return JsApiResult.success(JsonUtils.object("saved", true, DemoParams.KEY, key));
    }
}
