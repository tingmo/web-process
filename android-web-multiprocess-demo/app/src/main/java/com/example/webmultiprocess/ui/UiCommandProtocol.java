package com.example.webmultiprocess.ui;

import com.example.webmultiprocess.util.ProcessUtils;

import org.json.JSONException;
import org.json.JSONObject;

public final class UiCommandProtocol {
    public static final String COMMAND_DIALOG_CONFIRM = "dialog.confirm";

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
            json.put("commandId", "cmd_" + System.currentTimeMillis());
            json.put("pageId", pageId == null ? "" : pageId);
            json.put("command", command == null ? "" : command);
            json.put("sourceApi", sourceApi == null ? "" : sourceApi);
            json.put("payload", payload == null ? new JSONObject() : payload);
            json.put("timeoutMs", timeoutMs);
            json.put("timestamp", System.currentTimeMillis());
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
                "OK",
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
            json.put("commandId", commandId == null ? "" : commandId);
            json.put("pageId", pageId == null ? "" : pageId);
            json.put("command", command == null ? "" : command);
            json.put("success", success);
            json.put("code", code == null ? "" : code);
            json.put("message", message == null ? "" : message);
            json.put("data", data == null ? new JSONObject() : data);
            json.put("process", processName == null ? "" : processName);
            json.put("costMs", costMs);
            json.put("timestamp", System.currentTimeMillis());
        } catch (JSONException ignored) {
        }
        return json.toString();
    }

    static Meta parseMeta(String requestJson) {
        Meta meta = new Meta();
        try {
            JSONObject json = new JSONObject(requestJson);
            meta.commandId = json.optString("commandId");
            meta.pageId = json.optString("pageId");
            meta.command = json.optString("command");
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
