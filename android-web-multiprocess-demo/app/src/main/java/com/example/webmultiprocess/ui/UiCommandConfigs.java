package com.example.webmultiprocess.ui;

public final class UiCommandConfigs {
    public static final String COMMAND_ID_PREFIX = "cmd_";
    public static final long MIN_TIMEOUT_MS = 100L;

    public static final UiCommandConfig DIALOG_CONFIRM = new UiCommandConfig(
            "dialog.confirm",
            6000L,
            "UICommand from main process",
            "The JSAPI handler is running in main process and asks UI process to confirm.",
            "Confirm",
            "Cancel");

    private UiCommandConfigs() {
    }
}
