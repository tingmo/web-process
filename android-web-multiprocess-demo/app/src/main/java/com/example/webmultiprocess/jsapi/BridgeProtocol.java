package com.example.webmultiprocess.jsapi;

import org.json.JSONException;
import org.json.JSONObject;

public final class BridgeProtocol {
    public static final String BRIDGE_VERSION = "1.0.0";

    private BridgeProtocol() {
    }

    public static String successResponse(String requestJson, String processMode, JSONObject data, long costMs) {
        RequestMeta meta = parseMeta(requestJson);
        return response(meta.callbackId, meta.api, true, "OK", "success", data, processMode, costMs);
    }

    public static String errorResponse(String requestJson, String code, String message, String processMode) {
        RequestMeta meta = parseMeta(requestJson);
        return response(meta.callbackId, meta.api, false, code, message, new JSONObject(), processMode, 0L);
    }

    public static String response(
            String callbackId,
            String api,
            boolean success,
            String code,
            String message,
            JSONObject data,
            String processMode,
            long costMs) {
        JSONObject json = new JSONObject();
        try {
            json.put("bridgeVersion", BRIDGE_VERSION);
            json.put("callbackId", callbackId == null ? "" : callbackId);
            json.put("api", api == null ? "" : api);
            json.put("success", success);
            json.put("code", code == null ? "" : code);
            json.put("message", message == null ? "" : message);
            json.put("data", data == null ? new JSONObject() : data);
            json.put("processMode", processMode == null ? "" : processMode);
            json.put("costMs", costMs);
            json.put("timestamp", System.currentTimeMillis());
        } catch (JSONException ignored) {
        }
        return json.toString();
    }

    public static boolean isIpcTransportError(String responseJson) {
        try {
            JSONObject json = new JSONObject(responseJson);
            String code = json.optString("code");
            return "IPC_TIMEOUT".equals(code) || "IPC_NO_RESULT".equals(code) || "IPC_EXCEPTION".equals(code);
        } catch (JSONException ignored) {
            return true;
        }
    }

    private static RequestMeta parseMeta(String requestJson) {
        RequestMeta meta = new RequestMeta();
        try {
            JSONObject request = new JSONObject(requestJson);
            meta.callbackId = request.optString("callbackId");
            meta.api = request.optString("api");
        } catch (JSONException ignored) {
        }
        return meta;
    }

    private static final class RequestMeta {
        String callbackId = "";
        String api = "";
    }
}
