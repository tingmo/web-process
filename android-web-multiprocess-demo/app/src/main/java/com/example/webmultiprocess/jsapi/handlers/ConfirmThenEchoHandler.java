package com.example.webmultiprocess.jsapi.handlers;

import android.text.TextUtils;

import com.example.webmultiprocess.jsapi.ApiConfigs;
import com.example.webmultiprocess.jsapi.BridgeCodes;
import com.example.webmultiprocess.jsapi.ConfiguredJsApiHandler;
import com.example.webmultiprocess.jsapi.DemoParams;
import com.example.webmultiprocess.jsapi.JsonUtils;
import com.example.webmultiprocess.jsapi.JsApiContext;
import com.example.webmultiprocess.jsapi.JsApiResult;
import com.example.webmultiprocess.ui.UiCommandClient;
import com.example.webmultiprocess.ui.UiCommandConfig;
import com.example.webmultiprocess.ui.UiCommandConfigs;
import com.example.webmultiprocess.ui.UiCommandFields;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfirmThenEchoHandler extends ConfiguredJsApiHandler {
    public ConfirmThenEchoHandler() {
        super(ApiConfigs.DEMO_CONFIRM_THEN_ECHO);
    }

    @Override
    public JSONObject paramsSchema() {
        return JsonUtils.object(
                "type", "object",
                "properties", JsonUtils.object(
                        DemoParams.MESSAGE, JsonUtils.object("type", "string"),
                        DemoParams.PAYLOAD, JsonUtils.object("type", "object")));
    }

    @Override
    public JSONObject resultSchema() {
        return JsonUtils.object(
                "type", "object",
                "properties", JsonUtils.object(
                        DemoParams.CONFIRMED, JsonUtils.object("type", "boolean"),
                        UiCommandFields.PAGE_ID, JsonUtils.object("type", "string"),
                        DemoParams.UI_COMMAND_RESPONSE, JsonUtils.object("type", "object")));
    }

    @Override
    public JsApiResult handle(JsApiContext context, JSONObject params) {
        String pageId = context.getPageId();
        if (TextUtils.isEmpty(pageId)) {
            return JsApiResult.error(BridgeCodes.PAGE_ID_MISSING, "UI command requires pageId in request.");
        }

        UiCommandConfig confirmConfig = UiCommandConfigs.DIALOG_CONFIRM;
        JSONObject uiPayload = JsonUtils.object(
                UiCommandFields.TITLE, confirmConfig.defaultTitle(),
                UiCommandFields.MESSAGE, params.optString(DemoParams.MESSAGE, confirmConfig.defaultMessage()),
                UiCommandFields.POSITIVE_TEXT, confirmConfig.defaultPositiveText(),
                UiCommandFields.NEGATIVE_TEXT, confirmConfig.defaultNegativeText());

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
        if (!uiData.optBoolean(UiCommandFields.CONFIRMED)) {
            return JsApiResult.error(BridgeCodes.USER_CANCELLED, "User cancelled the UI command.");
        }

        return JsApiResult.success(JsonUtils.object(
                DemoParams.CONFIRMED, true,
                UiCommandFields.PAGE_ID, pageId,
                DemoParams.ECHO, params,
                DemoParams.UI_COMMAND_RESPONSE, parse(uiResponse)));
    }

    private JSONObject parse(String json) {
        try {
            return new JSONObject(json);
        } catch (JSONException ignored) {
            return new JSONObject();
        }
    }
}
