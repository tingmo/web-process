package com.example.webmultiprocess.ui;

public final class UiCommandContract {
    public static final String COMMAND_ID_PREFIX = "cmd_";
    public static final long MIN_TIMEOUT_MS = 100L;

    public static final CommandSpec DIALOG_CONFIRM = new CommandSpec(
            "dialog.confirm",
            6000L,
            "UICommand from main process",
            "The JSAPI handler is running in main process and asks UI process to confirm.",
            "Confirm",
            "Cancel");

    private UiCommandContract() {
    }

    public static final class CommandSpec {
        private final String command;
        private final long timeoutMs;
        private final String defaultTitle;
        private final String defaultMessage;
        private final String defaultPositiveText;
        private final String defaultNegativeText;

        public CommandSpec(
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

    public static final class Field {
        public static final String COMMAND_ID = "commandId";
        public static final String PAGE_ID = "pageId";
        public static final String COMMAND = "command";
        public static final String SOURCE_API = "sourceApi";
        public static final String PAYLOAD = "payload";
        public static final String TIMEOUT_MS = "timeoutMs";
        public static final String TIMESTAMP = "timestamp";
        public static final String SUCCESS = "success";
        public static final String CODE = "code";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String PROCESS = "process";
        public static final String COST_MS = "costMs";
        public static final String TITLE = "title";
        public static final String POSITIVE_TEXT = "positiveText";
        public static final String NEGATIVE_TEXT = "negativeText";
        public static final String CONFIRMED = "confirmed";

        private Field() {
        }
    }

    public static final class Code {
        public static final String OK = "OK";
        public static final String BAD_RESPONSE = "BAD_UI_COMMAND_RESPONSE";
        public static final String BAD_JSON = "BAD_UI_COMMAND_JSON";
        public static final String CONTEXT_UNAVAILABLE = "UI_CONTEXT_UNAVAILABLE";
        public static final String ACTIVITY_UNAVAILABLE = "ACTIVITY_UNAVAILABLE";
        public static final String COMMAND_NOT_FOUND = "UI_COMMAND_NOT_FOUND";
        public static final String TIMEOUT = "UI_COMMAND_TIMEOUT";
        public static final String IPC_EXCEPTION = "UI_COMMAND_IPC_EXCEPTION";
        public static final String APP_CONTEXT_MISSING = "APP_CONTEXT_MISSING";

        private Code() {
        }
    }
}
