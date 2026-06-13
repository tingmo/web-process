package com.example.webmultiprocess.jsapi;

import org.json.JSONException;
import org.json.JSONObject;

public final class BridgeProtocol {
    public static final String BRIDGE_VERSION = JsApiContract.BRIDGE_VERSION;

    private BridgeProtocol() {
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
            json.put(JsApiContract.Field.BRIDGE_VERSION, BRIDGE_VERSION);
            json.put(JsApiContract.Field.CALLBACK_ID, callbackId == null ? "" : callbackId);
            json.put(JsApiContract.Field.API, api == null ? "" : api);
            json.put(JsApiContract.Field.SUCCESS, success);
            json.put(JsApiContract.Field.CODE, code == null ? "" : code);
            json.put(JsApiContract.Field.MESSAGE, message == null ? "" : message);
            json.put(JsApiContract.Field.DATA, data == null ? new JSONObject() : data);
            json.put(JsApiContract.Field.PROCESS_MODE, processMode == null ? "" : processMode);
            json.put(JsApiContract.Field.COST_MS, costMs);
            json.put(JsApiContract.Field.TIMESTAMP, System.currentTimeMillis());
        } catch (JSONException ignored) {
        }
        return json.toString();
    }

    public static boolean isIpcTransportError(String responseJson) {
        try {
            JSONObject json = new JSONObject(responseJson);
            String code = json.optString(JsApiContract.Field.CODE);
            return JsApiContract.Code.IPC_TIMEOUT.equals(code)
                    || JsApiContract.Code.IPC_NO_RESULT.equals(code)
                    || JsApiContract.Code.IPC_EXCEPTION.equals(code);
        } catch (JSONException ignored) {
            return true;
        }
    }

    private static RequestMeta parseMeta(String requestJson) {
        RequestMeta meta = new RequestMeta();
        try {
            JSONObject request = new JSONObject(requestJson);
            meta.callbackId = request.optString(JsApiContract.Field.CALLBACK_ID);
            meta.api = request.optString(JsApiContract.Field.API);
        } catch (JSONException ignored) {
        }
        return meta;
    }

    private static final class RequestMeta {
        String callbackId = "";
        String api = "";
    }
}
