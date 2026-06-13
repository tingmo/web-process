package com.example.webmultiprocess.jsapi;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

public final class JsApiDispatcher {
    private JsApiDispatcher() {
    }

    public static String dispatch(
            Context context,
            String requestJson,
            String processMode,
            boolean localFallbackOnly) {
        long startMs = System.currentTimeMillis();
        String callbackId = "";
        String api = "";
        try {
            JSONObject request = new JSONObject(requestJson);
            callbackId = request.optString(BridgeFields.CALLBACK_ID);
            api = request.optString(BridgeFields.API);
            if (TextUtils.isEmpty(callbackId)) {
                return BridgeProtocol.response(
                        callbackId,
                        api,
                        false,
                        BridgeCodes.INVALID_CALLBACK,
                        "callbackId is required.",
                        new JSONObject(),
                        processMode,
                        elapsed(startMs));
            }
            if (TextUtils.isEmpty(api)) {
                return BridgeProtocol.response(
                        callbackId,
                        api,
                        false,
                        BridgeCodes.INVALID_API,
                        "api is required.",
                        new JSONObject(),
                        processMode,
                        elapsed(startMs));
            }

            JsApiHandler handler = JsApiRegistry.get(api);
            if (handler == null) {
                return BridgeProtocol.response(
                        callbackId,
                        api,
                        false,
                        BridgeCodes.API_NOT_FOUND,
                        "No JSAPI handler registered for " + api,
                        new JSONObject(),
                        processMode,
                        elapsed(startMs));
            }
            if (localFallbackOnly && !handler.allowLocalFallback()) {
                return BridgeProtocol.response(
                        callbackId,
                        api,
                        false,
                        BridgeCodes.PROCESS_UNAVAILABLE,
                        "Main process is unavailable and this JSAPI cannot run in local fallback.",
                        new JSONObject(),
                        processMode,
                        elapsed(startMs));
            }

            JSONObject params = request.optJSONObject(BridgeFields.PARAMS);
            if (params == null) {
                params = new JSONObject();
            }
            JsApiResult result =
                    handler.handle(new JsApiContext(context, request, processMode, localFallbackOnly), params);
            return BridgeProtocol.response(
                    callbackId,
                    api,
                    result.isSuccess(),
                    result.getCode(),
                    result.getMessage(),
                    result.getData(),
                    processMode,
                    elapsed(startMs));
        } catch (JSONException e) {
            return BridgeProtocol.response(
                    callbackId,
                    api,
                    false,
                    BridgeCodes.BAD_JSON,
                    e.getMessage(),
                    new JSONObject(),
                    processMode,
                    elapsed(startMs));
        } catch (Exception e) {
            return BridgeProtocol.response(
                    callbackId,
                    api,
                    false,
                    BridgeCodes.HANDLER_EXCEPTION,
                    e.getClass().getSimpleName() + ": " + e.getMessage(),
                    new JSONObject(),
                    processMode,
                    elapsed(startMs));
        }
    }

    private static long elapsed(long startMs) {
        return System.currentTimeMillis() - startMs;
    }
}
