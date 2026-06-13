package com.example.webmultiprocess.ui;

public interface UiSession {
    String getPageId();

    void dispatchUiCommand(String commandJson, UiCommandCallback callback);
}
