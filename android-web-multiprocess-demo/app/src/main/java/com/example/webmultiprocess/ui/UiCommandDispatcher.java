package com.example.webmultiprocess.ui;

import android.content.Context;

import com.example.webmultiprocess.util.ProcessUtils;

import org.json.JSONException;
import org.json.JSONObject;

public final class UiCommandDispatcher {
    private UiCommandDispatcher() {
    }

    public static void dispatch(
            Context context,
            final String commandJson,
            final UiCommandCallback callback) {
        final long startMs = System.currentTimeMillis();
        try {
            JSONObject request = new JSONObject(commandJson);
            String pageId = request.optString("pageId");
            UiSession session = UiSessionRegistry.get(pageId);
            if (session == null) {
                callback.onComplete(UiCommandProtocol.error(
                        commandJson,
                        "UI_CONTEXT_UNAVAILABLE",
                        "No live UI session for pageId=" + pageId,
                        ProcessUtils.currentProcessName(context),
                        elapsed(startMs)));
                return;
            }
            session.dispatchUiCommand(commandJson, new UiCommandCallback() {
                @Override
                public void onComplete(String responseJson) {
                    callback.onComplete(responseJson);
                }
            });
        } catch (JSONException e) {
            callback.onComplete(UiCommandProtocol.error(
                    commandJson,
                    "BAD_UI_COMMAND_JSON",
                    e.getMessage(),
                    ProcessUtils.currentProcessName(context),
                    elapsed(startMs)));
        }
    }

    private static long elapsed(long startMs) {
        return System.currentTimeMillis() - startMs;
    }
}
