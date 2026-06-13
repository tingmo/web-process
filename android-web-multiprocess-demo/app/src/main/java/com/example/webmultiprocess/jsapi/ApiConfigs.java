package com.example.webmultiprocess.jsapi;

public final class ApiConfigs {
    public static final String VERSION_1 = "1.0.0";

    public static final ApiConfig RUNTIME_GET_API_CATALOG = new ApiConfig(
            "runtime.getApiCatalog",
            VERSION_1,
            "Return the standardized JSAPI catalog exposed by native.",
            false,
            true);

    public static final ApiConfig DEVICE_GET_INFO = new ApiConfig(
            "device.getInfo",
            VERSION_1,
            "Return app, process, Android SDK, and device model information.",
            false,
            true);

    public static final ApiConfig UI_TOAST = new ApiConfig(
            "ui.toast",
            VERSION_1,
            "Show a short native Toast.",
            false,
            true);

    public static final ApiConfig STORAGE_SET = new ApiConfig(
            "storage.set",
            VERSION_1,
            "Persist a string value in main-process SharedPreferences.",
            true,
            false);

    public static final ApiConfig STORAGE_GET = new ApiConfig(
            "storage.get",
            VERSION_1,
            "Read a string value from main-process SharedPreferences.",
            true,
            false);

    public static final ApiConfig USER_GET_PROFILE = new ApiConfig(
            "user.getProfile",
            VERSION_1,
            "Return a mocked login profile owned by the main process.",
            true,
            false);

    public static final ApiConfig DEMO_ECHO = new ApiConfig(
            "demo.echo",
            VERSION_1,
            "A standard handler template for future JSAPI implementers.",
            false,
            true);

    public static final ApiConfig DEMO_CONFIRM_THEN_ECHO = new ApiConfig(
            "demo.confirmThenEcho",
            VERSION_1,
            "Mixed JSAPI demo: main-process business asks the UI process to execute a dialog UICommand.",
            true,
            false);

    private ApiConfigs() {
    }
}
