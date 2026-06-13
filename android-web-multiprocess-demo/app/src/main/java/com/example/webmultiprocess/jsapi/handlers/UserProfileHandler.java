package com.example.webmultiprocess.jsapi.handlers;

import com.example.webmultiprocess.jsapi.JsApiContract;
import com.example.webmultiprocess.jsapi.ConfiguredJsApiHandler;
import com.example.webmultiprocess.jsapi.JsonUtils;
import com.example.webmultiprocess.jsapi.JsApiContext;
import com.example.webmultiprocess.jsapi.JsApiResult;
import com.example.webmultiprocess.util.ProcessUtils;

import org.json.JSONObject;

public class UserProfileHandler extends ConfiguredJsApiHandler {
    public UserProfileHandler() {
        super(JsApiContract.USER_GET_PROFILE);
    }

    @Override
    public JSONObject paramsSchema() {
        return JsonUtils.object("type", "object", "properties", JsonUtils.object());
    }

    @Override
    public JSONObject resultSchema() {
        return JsonUtils.object(
                "type", "object",
                "properties", JsonUtils.object(
                        "userId", JsonUtils.object("type", "string"),
                        "nickname", JsonUtils.object("type", "string"),
                        "scopes", JsonUtils.object("type", "array")));
    }

    @Override
    public JsApiResult handle(JsApiContext context, JSONObject params) {
        JSONObject data = JsonUtils.object(
                "userId", JsApiContract.DemoUser.USER_ID,
                "nickname", JsApiContract.DemoUser.NICKNAME,
                "loginState", JsApiContract.DemoUser.LOGIN_STATE,
                "scopes", JsonUtils.array(
                        JsApiContract.DemoUser.SCOPE_PROFILE,
                        JsApiContract.DemoUser.SCOPE_STORAGE,
                        JsApiContract.DemoUser.SCOPE_TOAST),
                "ownerProcess", ProcessUtils.currentProcessName(context.getContext()));
        return JsApiResult.success(data);
    }
}
