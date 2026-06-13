package com.example.webmultiprocess.jsapi;

import org.json.JSONObject;

public final class JsApiResult {
    private final boolean success;
    private final String code;
    private final String message;
    private final JSONObject data;

    private JsApiResult(boolean success, String code, String message, JSONObject data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data == null ? new JSONObject() : data;
    }

    public static JsApiResult success(JSONObject data) {
        return new JsApiResult(true, "OK", "success", data);
    }

    public static JsApiResult error(String code, String message) {
        return new JsApiResult(false, code, message, new JSONObject());
    }

    public boolean isSuccess() {
        return success;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public JSONObject getData() {
        return data;
    }
}
