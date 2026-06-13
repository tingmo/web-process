package com.example.webmultiprocess.jsapi.handlers;

import com.example.webmultiprocess.jsapi.JsonUtils;
import com.example.webmultiprocess.jsapi.JsApiContext;
import com.example.webmultiprocess.jsapi.JsApiHandler;
import com.example.webmultiprocess.jsapi.JsApiResult;
import com.example.webmultiprocess.util.ProcessUtils;

import org.json.JSONObject;

public class DemoEchoHandler implements JsApiHandler {
    @Override
    public String name() {
        return "demo.echo";
    }

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public String description() {
        return "A standard handler template for future JSAPI implementers.";
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
                "properties", JsonUtils.object(
                        "message", JsonUtils.object("type", "string"),
                        "payload", JsonUtils.object("type", "object")));
    }

    @Override
    public JSONObject resultSchema() {
        return JsonUtils.object(
                "type", "object",
                "properties", JsonUtils.object(
                        "echo", JsonUtils.object("type", "object"),
                        "handledBy", JsonUtils.object("type", "string")));
    }

    @Override
    public JsApiResult handle(JsApiContext context, JSONObject params) {
        JSONObject data = JsonUtils.object(
                "echo", params,
                "handledBy", ProcessUtils.currentProcessName(context.getContext()),
                "processMode", context.getProcessMode(),
                "standard", JsonUtils.object(
                        "request", "callbackId + api + version + params + pageUrl",
                        "response", "callbackId + success + code + message + data + costMs"));
        return JsApiResult.success(data);
    }
}
