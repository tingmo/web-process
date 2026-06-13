package com.example.webmultiprocess.jsapi.handlers;

import com.example.webmultiprocess.jsapi.JsApiContract;
import com.example.webmultiprocess.jsapi.ConfiguredJsApiHandler;
import com.example.webmultiprocess.jsapi.JsonUtils;
import com.example.webmultiprocess.jsapi.JsApiContext;
import com.example.webmultiprocess.jsapi.JsApiResult;
import com.example.webmultiprocess.util.ProcessUtils;

import org.json.JSONObject;

public class DemoEchoHandler extends ConfiguredJsApiHandler {
    public DemoEchoHandler() {
        super(JsApiContract.DEMO_ECHO);
    }

    @Override
    public JSONObject paramsSchema() {
        return JsonUtils.object(
                "type", "object",
                "properties", JsonUtils.object(
                        JsApiContract.Param.MESSAGE, JsonUtils.object("type", "string"),
                        JsApiContract.Param.PAYLOAD, JsonUtils.object("type", "object")));
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
                JsApiContract.Param.ECHO, params,
                "handledBy", ProcessUtils.currentProcessName(context.getContext()),
                "processMode", context.getProcessMode(),
                "standard", JsonUtils.object(
                        "request", "callbackId + api + version + params + pageUrl",
                        "response", "callbackId + success + code + message + data + costMs"));
        return JsApiResult.success(data);
    }
}
