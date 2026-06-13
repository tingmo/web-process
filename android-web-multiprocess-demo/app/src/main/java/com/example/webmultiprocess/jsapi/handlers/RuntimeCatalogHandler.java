package com.example.webmultiprocess.jsapi.handlers;

import com.example.webmultiprocess.jsapi.BridgeProtocol;
import com.example.webmultiprocess.jsapi.JsonUtils;
import com.example.webmultiprocess.jsapi.JsApiContext;
import com.example.webmultiprocess.jsapi.JsApiHandler;
import com.example.webmultiprocess.jsapi.JsApiRegistry;
import com.example.webmultiprocess.jsapi.JsApiResult;
import com.example.webmultiprocess.util.ProcessUtils;

import org.json.JSONObject;

public class RuntimeCatalogHandler implements JsApiHandler {
    @Override
    public String name() {
        return "runtime.getApiCatalog";
    }

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public String description() {
        return "Return the standardized JSAPI catalog exposed by native.";
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
