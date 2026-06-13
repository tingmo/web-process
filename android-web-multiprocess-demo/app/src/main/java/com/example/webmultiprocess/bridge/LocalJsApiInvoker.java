package com.example.webmultiprocess.bridge;

import android.content.Context;

import com.example.webmultiprocess.jsapi.BridgeProtocol;
import com.example.webmultiprocess.jsapi.JsApiContract;
import com.example.webmultiprocess.jsapi.JsApiDispatcher;
import com.example.webmultiprocess.jsapi.JsApiRegistry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalJsApiInvoker implements JsApiInvoker {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private final Context context;
    private final boolean fallbackOnly;

    public LocalJsApiInvoker(Context context, boolean fallbackOnly) {
        this.context = context.getApplicationContext();
        this.fallbackOnly = fallbackOnly;
    }

    @Override
    public void invoke(final String requestJson, final Callback callback) {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                String response = JsApiDispatcher.dispatch(
                        context,
                        requestJson,
                        fallbackOnly ? "local_fallback" : "local_main",
                        fallbackOnly);
                callback.onComplete(response);
            }
        });
    }

    public boolean canHandleRequest(String requestJson) {
        try {
            JSONObject request = new JSONObject(requestJson);
            return JsApiRegistry.allowLocalFallback(request.optString(JsApiContract.Field.API));
        } catch (JSONException ignored) {
            return false;
        }
    }

    public String unavailableResponse(String requestJson) {
        return BridgeProtocol.errorResponse(
                requestJson,
                JsApiContract.Code.PROCESS_UNAVAILABLE,
                "Main process JSAPI service is unavailable and local fallback is not allowed for this API.",
                "local_fallback");
    }
}
