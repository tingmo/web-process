package com.example.webmultiprocess.bridge;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.example.webmultiprocess.jsapi.JsApiContract;
import com.example.webmultiprocess.jsapi.BridgeProtocol;
import com.example.webmultiprocess.util.ProcessUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class WebJsBridge {
    private final Context context;
    private final WeakReference<WebView> webViewRef;
    private final JsApiInvoker invoker;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final String modeLabel;
    private final String pageId;

    public WebJsBridge(
            Context context,
            WebView webView,
            JsApiInvoker invoker,
            String modeLabel,
            String pageId) {
        this.context = context.getApplicationContext();
        this.webViewRef = new WeakReference<>(webView);
        this.invoker = invoker;
        this.modeLabel = modeLabel;
        this.pageId = pageId;
    }

    @JavascriptInterface
    public void postMessage(String requestJson) {
        final String safeRequest = enrichRequest(requestJson == null ? "{}" : requestJson);
        invoker.invoke(safeRequest, new JsApiInvoker.Callback() {
            @Override
            public void onComplete(String responseJson) {
                dispatchToWeb(responseJson == null
                        ? BridgeProtocol.errorResponse(
                                safeRequest,
                                JsApiContract.Code.EMPTY_RESPONSE,
                                "Native returned empty response.",
                                modeLabel)
                        : responseJson);
            }
        });
    }

    @JavascriptInterface
    public String getBridgeInfo() {
        JSONObject json = new JSONObject();
        try {
            json.put(JsApiContract.Field.BRIDGE_VERSION, BridgeProtocol.BRIDGE_VERSION);
            json.put(JsApiContract.Field.MODE, modeLabel);
            json.put(JsApiContract.Field.PROCESS, ProcessUtils.currentProcessName(context));
            json.put(JsApiContract.Field.PAGE_ID, pageId);
        } catch (JSONException ignored) {
        }
        return json.toString();
    }

    private String enrichRequest(String requestJson) {
        try {
            JSONObject json = new JSONObject(requestJson);
            if (!json.has(JsApiContract.Field.PAGE_ID)) {
                json.put(JsApiContract.Field.PAGE_ID, pageId);
            }
            return json.toString();
        } catch (JSONException ignored) {
            return requestJson;
        }
    }

    private void dispatchToWeb(final String responseJson) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                WebView webView = webViewRef.get();
                if (webView == null) {
                    return;
                }
                String script = "window.WebNative&&window.WebNative.__callback("
                        + JSONObject.quote(responseJson)
                        + ");";
                webView.evaluateJavascript(script, null);
            }
        });
    }
}
