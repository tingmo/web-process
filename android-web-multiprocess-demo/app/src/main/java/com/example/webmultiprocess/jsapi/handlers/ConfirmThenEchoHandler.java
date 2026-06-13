package com.example.webmultiprocess.jsapi.handlers;

import android.text.TextUtils;

import com.example.webmultiprocess.jsapi.JsonUtils;
import com.example.webmultiprocess.jsapi.JsApiContext;
import com.example.webmultiprocess.jsapi.JsApiHandler;
import com.example.webmultiprocess.jsapi.JsApiResult;
import com.example.webmultiprocess.ui.UiCommandClient;
import com.example.webmultiprocess.ui.UiCommandProtocol;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfirmThenEchoHandler implements JsApiHandler {
    @Override
    public String name() {
        return "demo.confirmThenEcho";
    }

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public String description() {
        return "Mixed JSAPI demo: main-process business asks the UI process to execute a dialog UICommand.";
    }

    @Override
    public boolean mainProcessOnly() {
        return true;
    }

    @Override
    public boolean allowLocalFallback() {
        return false;
    }

    @Override
    public JSONObject paramsSchema() {
        return JsonUtils.object(
                "type", "object",
                "properties", JsonUtils.object(
                        "message", JsonUtils.object("type", "string"),
                        "payload", JsonUtils.object("type", "object")));
    }

    @Override
    public JSONObject resultSchema() {
        return JsonUtils.object(
                "type", "object",
                "properties", JsonUtils.object(
                        "confirmed", JsonUtils.object("type", "boolean"),
                        "pageId", JsonUtils.object("type", "string"),
                        "uiCommandResponse", JsonUtils.object("type", "object")));
    }

    @Override
    public JsApiResult handle(JsApiContext context, JSONObject params) {
        String pageId = context.getPageId();
        if (TextUtils.isEmpty(pageId)) {
            return JsApiResult.error("PAGE_ID_MISSING", "UI command requires pageId in request.");
        }

        JSONObject uiPayload = JsonUtils.object(
                "title", "UICommand from main process",
                "message", params.optString(
                        "message",
                        "The JSAPI handler is running in main process and asks UI process to confirm."),
                "positiveText", "Confirm",
                "negativeText", "Cancel");

        String uiResponse = UiCommandClient.dispatchSync(
                context.getContext(),
                pageId,
                UiCommandProtocol.COMMAND_DIALOG_CONFIRM,
                name(),
                uiPayload,
                6000L);

        if (!UiCommandClient.isSuccess(uiResponse)) {
            return JsApiResult.error(UiCommandClient.code(uiResponse), UiCommandClient.message(uiResponse));
        }

        JSONObject uiData = UiCommandClient.data(uiResponse);
        if (!uiData.optBoolean("confirmed")) {
            return JsApiResult.error("USER_CANCELLED", "User cancelled the UI command.");
        }

        return JsApiResult.success(JsonUtils.object(
                "confirmed", true,
                "pageId", pageId,
                "echo", params,
                "uiCommandResponse", parse(uiResponse)));
    }

    private JSONObject parse(String json) {
        try {
            return new JSONObject(json);
        } catch (JSONException ignored) {
            return new JSONObject();
        }
    }
}
