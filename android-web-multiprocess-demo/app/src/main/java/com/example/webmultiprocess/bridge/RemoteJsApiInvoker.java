package com.example.webmultiprocess.bridge;

import android.content.Context;

import com.example.webmultiprocess.ipc.InvokeJsApiTask;
import com.example.webmultiprocess.ipc.MainProcessIPCService;
import com.example.webmultiprocess.jsapi.BridgeCodes;
import com.example.webmultiprocess.jsapi.BridgeProtocol;

import cc.suitalk.ipcinvoker.IPCInvokeCallback;
import cc.suitalk.ipcinvoker.IPCTask;

public class RemoteJsApiInvoker implements JsApiInvoker {
    private final LocalJsApiInvoker localFallbackInvoker;

    public RemoteJsApiInvoker(Context context) {
        this.localFallbackInvoker = new LocalJsApiInvoker(context, true);
    }

    @Override
    public void invoke(final String requestJson, final Callback callback) {
        final String defaultResult = BridgeProtocol.errorResponse(
                requestJson,
                BridgeCodes.IPC_TIMEOUT,
                "IPCInvoker call to main process timed out.",
                "web_to_main");
        try {
            IPCTask.create(MainProcessIPCService.PROCESS_NAME)
                    .timeout(5000)
                    .async(InvokeJsApiTask.class)
                    .data(requestJson)
                    .defaultResult(defaultResult)
                    .callback(true, new IPCInvokeCallback<String>() {
                        @Override
                        public void onCallback(String responseJson) {
                            completeOrFallback(requestJson, responseJson, callback);
                        }
                    })
                    .invoke();
        } catch (Throwable throwable) {
            String response = BridgeProtocol.errorResponse(
                    requestJson,
                    BridgeCodes.IPC_EXCEPTION,
                    throwable.getClass().getSimpleName() + ": " + throwable.getMessage(),
                    "web_to_main");
            completeOrFallback(requestJson, response, callback);
        }
    }

    private void completeOrFallback(
            String requestJson,
            String responseJson,
            Callback callback) {
        if (responseJson == null) {
            responseJson = BridgeProtocol.errorResponse(
                    requestJson,
                    BridgeCodes.IPC_NO_RESULT,
                    "IPCInvoker returned an empty result.",
                    "web_to_main");
        }
        if (BridgeProtocol.isIpcTransportError(responseJson)
                && localFallbackInvoker.canHandleRequest(requestJson)) {
            localFallbackInvoker.invoke(requestJson, callback);
            return;
        }
        if (BridgeProtocol.isIpcTransportError(responseJson)
                && !localFallbackInvoker.canHandleRequest(requestJson)) {
            callback.onComplete(localFallbackInvoker.unavailableResponse(requestJson));
            return;
        }
        callback.onComplete(responseJson);
    }
}
