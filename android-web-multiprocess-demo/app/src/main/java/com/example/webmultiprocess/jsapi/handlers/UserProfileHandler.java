package com.example.webmultiprocess.jsapi.handlers;

import com.example.webmultiprocess.jsapi.JsonUtils;
import com.example.webmultiprocess.jsapi.JsApiContext;
import com.example.webmultiprocess.jsapi.JsApiHandler;
import com.example.webmultiprocess.jsapi.JsApiResult;
import com.example.webmultiprocess.util.ProcessUtils;

import org.json.JSONObject;

public class UserProfileHandler implements JsApiHandler {
    @Override
    public String name() {
        return "user.getProfile";
    }

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public String description() {
        return "Return a mocked login profile owned by the main process.";
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
                "userId", "demo-user-10001",
                "nickname", "IPCInvoker Demo User",
                "loginState", "mocked",
                "scopes", JsonUtils.array("profile", "storage", "toast"),
                "ownerProcess", ProcessUtils.currentProcessName(context.getContext()));
        return JsApiResult.success(data);
    }
}
