package com.example.webmultiprocess.ui;

import org.json.JSONException;
import org.json.JSONObject;

public final class UiCommandProtocol {
    private UiCommandProtocol() {
    }

    public static String command(
            String pageId,
            String command,
            String sourceApi,
            JSONObject payload,
            long timeoutMs) {
        JSONObject json = new JSONObject();
        try {
            json.put(UiCommandContract.Field.COMMAND_ID, UiCommandContract.COMMAND_ID_PREFIX + System.currentTimeMillis());
            json.put(UiCommandContract.Field.PAGE_ID, pageId == null ? "" : pageId);
            json.put(UiCommandContract.Field.COMMAND, command == null ? "" : command);
            json.put(UiCommandContract.Field.SOURCE_API, sourceApi == null ? "" : sourceApi);
            json.put(UiCommandContract.Field.PAYLOAD, payload == null ? new JSONObject() : payload);
            json.put(UiCommandContract.Field.TIMEOUT_MS, timeoutMs);
            json.put(UiCommandContract.Field.TIMESTAMP, System.currentTimeMillis());
        } catch (JSONException ignored) {
        }
        return json.toString();
    }

    public static String success(String requestJson, JSONObject data, String processName, long costMs) {
        Meta meta = parseMeta(requestJson);
        return response(
                meta.commandId,
                meta.pageId,
                meta.command,
                true,
                UiCommandContract.Code.OK,
                "success",
                data,
                processName,
                costMs);
    }

    public static String error(
            String requestJson,
            String code,
            String message,
            String processName,
            long costMs) {
        Meta meta = parseMeta(requestJson);
        return response(
                meta.commandId,
                meta.pageId,
                meta.command,
                false,
                code,
                message,
                new JSONObject(),
                processName,
                costMs);
    }

    public static String response(
            String commandId,
            String pageId,
            String command,
            boolean success,
            String code,
            String message,
            JSONObject data,
            String processName,
            long costMs) {
        JSONObject json = new JSONObject();
        try {
            json.put(UiCommandContract.Field.COMMAND_ID, commandId == null ? "" : commandId);
            json.put(UiCommandContract.Field.PAGE_ID, pageId == null ? "" : pageId);
            json.put(UiCommandContract.Field.COMMAND, command == null ? "" : command);
            json.put(UiCommandContract.Field.SUCCESS, success);
            json.put(UiCommandContract.Field.CODE, code == null ? "" : code);
            json.put(UiCommandContract.Field.MESSAGE, message == null ? "" : message);
            json.put(UiCommandContract.Field.DATA, data == null ? new JSONObject() : data);
            json.put(UiCommandContract.Field.PROCESS, processName == null ? "" : processName);
            json.put(UiCommandContract.Field.COST_MS, costMs);
            json.put(UiCommandContract.Field.TIMESTAMP, System.currentTimeMillis());
        } catch (JSONException ignored) {
        }
        return json.toString();
    }

    static Meta parseMeta(String requestJson) {
        Meta meta = new Meta();
        try {
            JSONObject json = new JSONObject(requestJson);
            meta.commandId = json.optString(UiCommandContract.Field.COMMAND_ID);
            meta.pageId = json.optString(UiCommandContract.Field.PAGE_ID);
            meta.command = json.optString(UiCommandContract.Field.COMMAND);
        } catch (JSONException ignored) {
        }
        return meta;
    }

    static final class Meta {
        String commandId = "";
        String pageId = "";
        String command = "";
    }
}
