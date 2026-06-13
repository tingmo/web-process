package com.example.webmultiprocess.jsapi;

public final class JsApiContract {
    public static final String BRIDGE_VERSION = "1.0.0";

    public static final ApiSpec RUNTIME_GET_API_CATALOG = new ApiSpec(
            "runtime.getApiCatalog",
            BRIDGE_VERSION,
            "Return the standardized JSAPI catalog exposed by native.",
            false,
            true);

    public static final ApiSpec DEVICE_GET_INFO = new ApiSpec(
            "device.getInfo",
            BRIDGE_VERSION,
            "Return app, process, Android SDK, and device model information.",
            false,
            true);

    public static final ApiSpec UI_TOAST = new ApiSpec(
            "ui.toast",
            BRIDGE_VERSION,
            "Show a short native Toast.",
            false,
            true);

    public static final ApiSpec STORAGE_SET = new ApiSpec(
            "storage.set",
            BRIDGE_VERSION,
            "Persist a string value in main-process SharedPreferences.",
            true,
            false);

    public static final ApiSpec STORAGE_GET = new ApiSpec(
            "storage.get",
            BRIDGE_VERSION,
            "Read a string value from main-process SharedPreferences.",
            true,
            false);

    public static final ApiSpec USER_GET_PROFILE = new ApiSpec(
            "user.getProfile",
            BRIDGE_VERSION,
            "Return a mocked login profile owned by the main process.",
            true,
            false);

    public static final ApiSpec DEMO_ECHO = new ApiSpec(
            "demo.echo",
            BRIDGE_VERSION,
            "A standard handler template for future JSAPI implementers.",
            false,
            true);

    public static final ApiSpec DEMO_CONFIRM_THEN_ECHO = new ApiSpec(
            "demo.confirmThenEcho",
            BRIDGE_VERSION,
            "Mixed JSAPI demo: main-process business asks the UI process to execute a dialog UICommand.",
            true,
            false);

    private JsApiContract() {
    }

    public static final class ApiSpec {
        private final String name;
        private final String version;
        private final String description;
        private final boolean mainProcessOnly;
        private final boolean allowLocalFallback;

        public ApiSpec(
                String name,
                String version,
                String description,
                boolean mainProcessOnly,
                boolean allowLocalFallback) {
            this.name = name;
            this.version = version;
            this.description = description;
            this.mainProcessOnly = mainProcessOnly;
            this.allowLocalFallback = allowLocalFallback;
        }

        public String name() {
            return name;
        }

        public String version() {
            return version;
        }

        public String description() {
            return description;
        }

        public boolean mainProcessOnly() {
            return mainProcessOnly;
        }

        public boolean allowLocalFallback() {
            return allowLocalFallback;
        }
    }

    public static final class Field {
        public static final String BRIDGE_VERSION = "bridgeVersion";
        public static final String CALLBACK_ID = "callbackId";
        public static final String API = "api";
        public static final String PARAMS = "params";
        public static final String PAGE_ID = "pageId";
        public static final String MODE = "mode";
        public static final String PROCESS = "process";
        public static final String SUCCESS = "success";
        public static final String CODE = "code";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String PROCESS_MODE = "processMode";
        public static final String COST_MS = "costMs";
        public static final String TIMESTAMP = "timestamp";

        private Field() {
        }
    }

    public static final class Code {
        public static final String OK = "OK";
        public static final String BAD_JSON = "BAD_JSON";
        public static final String INVALID_CALLBACK = "INVALID_CALLBACK";
        public static final String INVALID_API = "INVALID_API";
        public static final String API_NOT_FOUND = "API_NOT_FOUND";
        public static final String INVALID_PARAM = "INVALID_PARAM";
        public static final String HANDLER_EXCEPTION = "HANDLER_EXCEPTION";
        public static final String IPC_TIMEOUT = "IPC_TIMEOUT";
        public static final String IPC_NO_RESULT = "IPC_NO_RESULT";
        public static final String IPC_EXCEPTION = "IPC_EXCEPTION";
        public static final String PROCESS_UNAVAILABLE = "PROCESS_UNAVAILABLE";
        public static final String PAGE_ID_MISSING = "PAGE_ID_MISSING";
        public static final String USER_CANCELLED = "USER_CANCELLED";
        public static final String APP_CONTEXT_MISSING = "APP_CONTEXT_MISSING";
        public static final String EMPTY_RESPONSE = "EMPTY_RESPONSE";

        private Code() {
        }
    }

    public static final class Catalog {
        public static final String NAME = "name";
        public static final String VERSION = "version";
        public static final String DESCRIPTION = "description";
        public static final String MAIN_PROCESS_ONLY = "mainProcessOnly";
        public static final String ALLOW_LOCAL_FALLBACK = "allowLocalFallback";
        public static final String PARAMS_SCHEMA = "paramsSchema";
        public static final String RESULT_SCHEMA = "resultSchema";

        private Catalog() {
        }
    }

    public static final class Param {
        public static final String KEY = "key";
        public static final String VALUE = "value";
        public static final String MESSAGE = "message";
        public static final String LONG = "long";
        public static final String PAYLOAD = "payload";
        public static final String CONFIRMED = "confirmed";
        public static final String ECHO = "echo";
        public static final String UI_COMMAND_RESPONSE = "uiCommandResponse";

        private Param() {
        }
    }

    public static final class DemoStorage {
        public static final String PREFERENCES_NAME = "jsapi_storage";

        private DemoStorage() {
        }
    }

    public static final class DemoUser {
        public static final String USER_ID = "demo-user-10001";
        public static final String NICKNAME = "IPCInvoker Demo User";
        public static final String LOGIN_STATE = "mocked";
        public static final String SCOPE_PROFILE = "profile";
        public static final String SCOPE_STORAGE = "storage";
        public static final String SCOPE_TOAST = "toast";

        private DemoUser() {
        }
    }
}
