package com.example.webmultiprocess.ipc;

import android.content.Context;

import com.example.webmultiprocess.DemoApplication;
import com.example.webmultiprocess.jsapi.BridgeCodes;
import com.example.webmultiprocess.jsapi.BridgeProtocol;
import com.example.webmultiprocess.jsapi.JsApiDispatcher;

import cc.suitalk.ipcinvoker.IPCAsyncInvokeTask;
import cc.suitalk.ipcinvoker.IPCInvokeCallback;

public class InvokeJsApiTask implements IPCAsyncInvokeTask<String, String> {
    @Override
    public void invoke(String requestJson, IPCInvokeCallback<String> callback) {
        Context context = DemoApplication.getAppContext();
        String response;
        if (context == null) {
            response = BridgeProtocol.errorResponse(
                    requestJson,
                    BridgeCodes.APP_CONTEXT_MISSING,
                    "Main process application context is unavailable.",
                    "main");
        } else {
            response = JsApiDispatcher.dispatch(context, requestJson, "main", false);
        }
        callback.onCallback(response);
    }
}
