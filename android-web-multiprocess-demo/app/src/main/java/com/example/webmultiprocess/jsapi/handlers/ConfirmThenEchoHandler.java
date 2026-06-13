package com.example.webmultiprocess.jsapi.handlers;

import android.text.TextUtils;

import com.example.webmultiprocess.jsapi.JsApiContract;
import com.example.webmultiprocess.jsapi.ConfiguredJsApiHandler;
import com.example.webmultiprocess.jsapi.JsonUtils;
import com.example.webmultiprocess.jsapi.JsApiContext;
import com.example.webmultiprocess.jsapi.JsApiResult;
import com.example.webmultiprocess.ui.UiCommandClient;
import com.example.webmultiprocess.ui.UiCommandContract;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfirmThenEchoHandler extends ConfiguredJsApiHandler {
    public ConfirmThenEchoHandler() {
        super(JsApiContract.DEMO_CONFIRM_THEN_ECHO);
    }

    @Override
    public JSONObject paramsSchema() {
        return JsonUtils.object(
                "type", "object",
                "properties", JsonUtils.object(
                        JsApiContract.Param.MESSAGE, JsonUtils.object("type", "string"),
                        JsApiContract.Param.PAYLOAD, JsonUtils.object("type", "object")));
    }

    @Override
    public JSONObject resultSchema() {
        return JsonUtils.object(
                "type", "object",
                "properties", JsonUtils.object(
                        JsApiContract.Param.CONFIRMED, JsonUtils.object("type", "boolean"),
                        UiCommandContract.Field.PAGE_ID, JsonUtils.object("type", "string"),
                        JsApiContract.Param.UI_COMMAND_RESPONSE, JsonUtils.object("type", "object")));
    }

    @Override
    public JsApiResult handle(JsApiContext context, JSONObject params) {
        String pageId = context.getPageId();
        if (TextUtils.isEmpty(pageId)) {
            return JsApiResult.error(JsApiContract.Code.PAGE_ID_MISSING, "UI command requires pageId in request.");
        }

        UiCommandContract.CommandSpec confirmConfig = UiCommandContract.DIALOG_CONFIRM;
        JSONObject uiPayload = JsonUtils.object(
                UiCommandContract.Field.TITLE, confirmConfig.defaultTitle(),
                UiCommandContract.Field.MESSAGE, params.optString(JsApiContract.Param.MESSAGE, confirmConfig.defaultMessage()),
                UiCommandContract.Field.POSITIVE_TEXT, confirmConfig.defaultPositiveText(),
                UiCommandContract.Field.NEGATIVE_TEXT, confirmConfig.defaultNegativeText());

        String uiResponse = UiCommandClient.dispatchSync(
                context.getContext(),
                pageId,
                confirmConfig.command(),
                name(),
                uiPayload,
                confirmConfig.timeoutMs());

        if (!UiCommandClient.isSuccess(uiResponse)) {
            return JsApiResult.error(UiCommandClient.code(uiResponse), UiCommandClient.message(uiResponse));
        }

        JSONObject uiData = UiCommandClient.data(uiResponse);
        if (!uiData.optBoolean(UiCommandContract.Field.CONFIRMED)) {
            return JsApiResult.error(JsApiContract.Code.USER_CANCELLED, "User cancelled the UI command.");
        }

        return JsApiResult.success(JsonUtils.object(
                JsApiContract.Param.CONFIRMED, true,
                UiCommandContract.Field.PAGE_ID, pageId,
                JsApiContract.Param.ECHO, params,
                JsApiContract.Param.UI_COMMAND_RESPONSE, parse(uiResponse)));
    }

    private JSONObject parse(String json) {
        try {
            return new JSONObject(json);
        } catch (JSONException ignored) {
            return new JSONObject();
        }
    }
}
