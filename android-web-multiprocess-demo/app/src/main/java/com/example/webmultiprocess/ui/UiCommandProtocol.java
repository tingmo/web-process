package com.example.webmultiprocess.ui;

import com.example.webmultiprocess.util.ProcessUtils;

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
            json.put(UiCommandFields.COMMAND_ID, UiCommandConfigs.COMMAND_ID_PREFIX + System.currentTimeMillis());
            json.put(UiCommandFields.PAGE_ID, pageId == null ? "" : pageId);
            json.put(UiCommandFields.COMMAND, command == null ? "" : command);
            json.put(UiCommandFields.SOURCE_API, sourceApi == null ? "" : sourceApi);
            json.put(UiCommandFields.PAYLOAD, payload == null ? new JSONObject() : payload);
            json.put(UiCommandFields.TIMEOUT_MS, timeoutMs);
            json.put(UiCommandFields.TIMESTAMP, System.currentTimeMillis());
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
                UiCommandCodes.OK,
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
            json.put(UiCommandFields.COMMAND_ID, commandId == null ? "" : commandId);
            json.put(UiCommandFields.PAGE_ID, pageId == null ? "" : pageId);
            json.put(UiCommandFields.COMMAND, command == null ? "" : command);
            json.put(UiCommandFields.SUCCESS, success);
            json.put(UiCommandFields.CODE, code == null ? "" : code);
            json.put(UiCommandFields.MESSAGE, message == null ? "" : message);
            json.put(UiCommandFields.DATA, data == null ? new JSONObject() : data);
            json.put(UiCommandFields.PROCESS, processName == null ? "" : processName);
            json.put(UiCommandFields.COST_MS, costMs);
            json.put(UiCommandFields.TIMESTAMP, System.currentTimeMillis());
        } catch (JSONException ignored) {
        }
        return json.toString();
    }

    static Meta parseMeta(String requestJson) {
        Meta meta = new Meta();
        try {
            JSONObject json = new JSONObject(requestJson);
            meta.commandId = json.optString(UiCommandFields.COMMAND_ID);
            meta.pageId = json.optString(UiCommandFields.PAGE_ID);
            meta.command = json.optString(UiCommandFields.COMMAND);
        } catch (JSONException ignored) {
        }
        return meta;
    }

    static String processName(android.content.Context context) {
        return ProcessUtils.currentProcessName(context);
    }

    static final class Meta {
        String commandId = "";
        String pageId = "";
        String command = "";
    }
}
