package com.example.webmultiprocess.jsapi;

import org.json.JSONObject;

public interface JsApiHandler {
    String name();

    String version();

    String description();

    boolean mainProcessOnly();

    boolean allowLocalFallback();

    JSONObject paramsSchema();

    JSONObject resultSchema();

    JsApiResult handle(JsApiContext context, JSONObject params) throws Exception;
}
