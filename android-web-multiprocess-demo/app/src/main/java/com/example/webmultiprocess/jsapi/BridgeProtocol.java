package com.example.webmultiprocess.jsapi;

import org.json.JSONException;
import org.json.JSONObject;

public final class BridgeProtocol {
    public static final String BRIDGE_VERSION = "1.0.0";

    private BridgeProtocol() {
    }

    public static String successResponse(String requestJson, String processMode, JSONObject data, long costMs) {
        RequestMeta meta = parseMeta(requestJson);
        return response(meta.callbackId, meta.api, true, BridgeCodes.OK, "success", data, processMode, costMs);
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
            json.put(BridgeFields.BRIDGE_VERSION, BRIDGE_VERSION);
            json.put(BridgeFields.CALLBACK_ID, callbackId == null ? "" : callbackId);
            json.put(BridgeFields.API, api == null ? "" : api);
            json.put(BridgeFields.SUCCESS, success);
            json.put(BridgeFields.CODE, code == null ? "" : code);
            json.put(BridgeFields.MESSAGE, message == null ? "" : message);
            json.put(BridgeFields.DATA, data == null ? new JSONObject() : data);
            json.put(BridgeFields.PROCESS_MODE, processMode == null ? "" : processMode);
            json.put(BridgeFields.COST_MS, costMs);
            json.put(BridgeFields.TIMESTAMP, System.currentTimeMillis());
        } catch (JSONException ignored) {
        }
        return json.toString();
    }

    public static boolean isIpcTransportError(String responseJson) {
        try {
            JSONObject json = new JSONObject(responseJson);
            String code = json.optString(BridgeFields.CODE);
            return BridgeCodes.IPC_TIMEOUT.equals(code)
                    || BridgeCodes.IPC_NO_RESULT.equals(code)
                    || BridgeCodes.IPC_EXCEPTION.equals(code);
        } catch (JSONException ignored) {
            return true;
        }
    }

    private static RequestMeta parseMeta(String requestJson) {
        RequestMeta meta = new RequestMeta();
        try {
            JSONObject request = new JSONObject(requestJson);
            meta.callbackId = request.optString(BridgeFields.CALLBACK_ID);
            meta.api = request.optString(BridgeFields.API);
        } catch (JSONException ignored) {
        }
        return meta;
    }

    private static final class RequestMeta {
        String callbackId = "";
        String api = "";
    }
}
