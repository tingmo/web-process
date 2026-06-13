package com.example.webmultiprocess.jsapi.handlers;

import com.example.webmultiprocess.jsapi.JsApiContract;
import com.example.webmultiprocess.jsapi.BridgeProtocol;
import com.example.webmultiprocess.jsapi.ConfiguredJsApiHandler;
import com.example.webmultiprocess.jsapi.JsonUtils;
import com.example.webmultiprocess.jsapi.JsApiContext;
import com.example.webmultiprocess.jsapi.JsApiRegistry;
import com.example.webmultiprocess.jsapi.JsApiResult;
import com.example.webmultiprocess.util.ProcessUtils;

import org.json.JSONObject;

public class RuntimeCatalogHandler extends ConfiguredJsApiHandler {
    public RuntimeCatalogHandler() {
        super(JsApiContract.RUNTIME_GET_API_CATALOG);
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
                        "bridgeVersion", JsonUtils.object("type", "string"),
                        "process", JsonUtils.object("type", "string"),
                        "apis", JsonUtils.object("type", "array")));
    }

    @Override
    public JsApiResult handle(JsApiContext context, JSONObject params) {
        JSONObject data = JsonUtils.object(
                "bridgeVersion", BridgeProtocol.BRIDGE_VERSION,
                "process", ProcessUtils.currentProcessName(context.getContext()),
                "processMode", context.getProcessMode(),
                "localFallbackOnly", context.isLocalFallbackOnly(),
                "apis", JsApiRegistry.catalogJson());
        return JsApiResult.success(data);
    }
}
