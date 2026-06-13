package com.example.webmultiprocess.jsapi;

import android.content.Context;

import org.json.JSONObject;

public final class JsApiContext {
    private final Context context;
    private final JSONObject request;
    private final String processMode;
    private final boolean localFallbackOnly;

    JsApiContext(Context context, JSONObject request, String processMode, boolean localFallbackOnly) {
        this.context = context.getApplicationContext();
        this.request = request;
        this.processMode = processMode;
        this.localFallbackOnly = localFallbackOnly;
    }

    public Context getContext() {
        return context;
    }

    public JSONObject getRequest() {
        return request;
    }

    public String getProcessMode() {
        return processMode;
    }

    public boolean isLocalFallbackOnly() {
        return localFallbackOnly;
    }

    public String getPageUrl() {
        return request.optString("pageUrl");
    }
}
