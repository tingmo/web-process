package com.example.webmultiprocess.ui;

public interface UiSession {
    String getPageId();

    void dispatchUiCommand(String commandJson, Callback callback);

    interface Callback {
        void onComplete(String responseJson);
    }
}
