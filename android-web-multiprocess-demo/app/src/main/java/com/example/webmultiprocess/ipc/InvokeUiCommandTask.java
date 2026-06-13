package com.example.webmultiprocess.ipc;

import android.content.Context;

import com.example.webmultiprocess.DemoApplication;
import com.example.webmultiprocess.ui.UiCommandCallback;
import com.example.webmultiprocess.ui.UiCommandDispatcher;
import com.example.webmultiprocess.ui.UiCommandProtocol;

import cc.suitalk.ipcinvoker.IPCAsyncInvokeTask;
import cc.suitalk.ipcinvoker.IPCInvokeCallback;

public class InvokeUiCommandTask implements IPCAsyncInvokeTask<String, String> {
    @Override
    public void invoke(final String commandJson, final IPCInvokeCallback<String> callback) {
        Context context = DemoApplication.getAppContext();
        if (context == null) {
            callback.onCallback(UiCommandProtocol.error(
                    commandJson,
                    "APP_CONTEXT_MISSING",
                    "Web process application context is unavailable.",
                    "",
                    0L));
            return;
        }
        UiCommandDispatcher.dispatch(context, commandJson, new UiCommandCallback() {
            @Override
            public void onComplete(String responseJson) {
                callback.onCallback(responseJson);
            }
        });
    }
}
