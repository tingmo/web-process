package com.example.webmultiprocess.bridge;

public interface JsApiInvoker {
    void invoke(String requestJson, Callback callback);

    interface Callback {
        void onComplete(String responseJson);
    }
}
