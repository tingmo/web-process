package com.example.webmultiprocess.jsapi;

import android.content.Context;

import org.json.JSONObject;

public final class JsApiContext {
    private final Context context;
    private final String pageId;
    private final String processMode;
    private final boolean localFallbackOnly;

    JsApiContext(Context context, JSONObject request, String processMode, boolean localFallbackOnly) {
        this.context = context.getApplicationContext();
        this.pageId = request.optString(JsApiContract.Field.PAGE_ID);
        this.processMode = processMode;
        this.localFallbackOnly = localFallbackOnly;
    }

    public Context getContext() {
        return context;
    }

    public String getProcessMode() {
        return processMode;
    }

    public boolean isLocalFallbackOnly() {
        return localFallbackOnly;
    }

    public String getPageId() {
        return pageId;
    }
}
