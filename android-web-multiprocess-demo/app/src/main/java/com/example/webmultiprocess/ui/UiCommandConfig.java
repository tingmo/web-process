package com.example.webmultiprocess.ui;

public final class UiCommandConfig {
    private final String command;
    private final long timeoutMs;
    private final String defaultTitle;
    private final String defaultMessage;
    private final String defaultPositiveText;
    private final String defaultNegativeText;

    public UiCommandConfig(
            String command,
            long timeoutMs,
            String defaultTitle,
            String defaultMessage,
            String defaultPositiveText,
            String defaultNegativeText) {
        this.command = command;
        this.timeoutMs = timeoutMs;
        this.defaultTitle = defaultTitle;
        this.defaultMessage = defaultMessage;
        this.defaultPositiveText = defaultPositiveText;
        this.defaultNegativeText = defaultNegativeText;
    }

    public String command() {
        return command;
    }

    public long timeoutMs() {
        return timeoutMs;
    }

    public String defaultTitle() {
        return defaultTitle;
    }

    public String defaultMessage() {
        return defaultMessage;
    }

    public String defaultPositiveText() {
        return defaultPositiveText;
    }

    public String defaultNegativeText() {
        return defaultNegativeText;
    }
}
