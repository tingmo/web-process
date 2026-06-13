package com.example.webmultiprocess.ui;

public final class UiCommandCodes {
    public static final String OK = "OK";
    public static final String BAD_RESPONSE = "BAD_UI_COMMAND_RESPONSE";
    public static final String BAD_JSON = "BAD_UI_COMMAND_JSON";
    public static final String CONTEXT_UNAVAILABLE = "UI_CONTEXT_UNAVAILABLE";
    public static final String ACTIVITY_UNAVAILABLE = "ACTIVITY_UNAVAILABLE";
    public static final String COMMAND_NOT_FOUND = "UI_COMMAND_NOT_FOUND";
    public static final String TIMEOUT = "UI_COMMAND_TIMEOUT";
    public static final String IPC_EXCEPTION = "UI_COMMAND_IPC_EXCEPTION";
    public static final String APP_CONTEXT_MISSING = "APP_CONTEXT_MISSING";

    private UiCommandCodes() {
    }
}
