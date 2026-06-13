package com.example.webmultiprocess.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;

import com.example.webmultiprocess.util.ProcessUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class WebUiSession implements UiSession {
    private final String pageId;
    private final WeakReference<Activity> activityRef;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public WebUiSession(String pageId, Activity activity) {
        this.pageId = pageId;
        this.activityRef = new WeakReference<>(activity);
    }

    @Override
    public String getPageId() {
        return pageId;
    }

    @Override
    public void dispatchUiCommand(final String commandJson, final UiCommandCallback callback) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                dispatchOnMainThread(commandJson, callback);
            }
        });
    }

    private void dispatchOnMainThread(String commandJson, UiCommandCallback callback) {
        long startMs = System.currentTimeMillis();
        Activity activity = activityRef.get();
        if (activity == null || activity.isFinishing()) {
            callback.onComplete(UiCommandProtocol.error(
                    commandJson,
                    UiCommandCodes.ACTIVITY_UNAVAILABLE,
                    "The UI host Activity is gone.",
                    processName(activity),
                    elapsed(startMs)));
            return;
        }

        try {
            JSONObject request = new JSONObject(commandJson);
            String command = request.optString(UiCommandFields.COMMAND);
            if (UiCommandConfigs.DIALOG_CONFIRM.command().equals(command)) {
                showConfirmDialog(activity, commandJson, request, callback, startMs);
                return;
            }
            callback.onComplete(UiCommandProtocol.error(
                    commandJson,
                    UiCommandCodes.COMMAND_NOT_FOUND,
                    "Unsupported UI command: " + command,
                    processName(activity),
                    elapsed(startMs)));
        } catch (JSONException e) {
            callback.onComplete(UiCommandProtocol.error(
                    commandJson,
                    UiCommandCodes.BAD_JSON,
                    e.getMessage(),
                    processName(activity),
                    elapsed(startMs)));
        }
    }

    private void showConfirmDialog(
            final Activity activity,
            final String commandJson,
            JSONObject request,
            final UiCommandCallback callback,
            final long startMs) {
        JSONObject payload = request.optJSONObject(UiCommandFields.PAYLOAD);
        if (payload == null) {
            payload = new JSONObject();
        }
        UiCommandConfig config = UiCommandConfigs.DIALOG_CONFIRM;
        String title = payload.optString(UiCommandFields.TITLE, config.defaultTitle());
        String message = payload.optString(UiCommandFields.MESSAGE, config.defaultMessage());
        String positive = payload.optString(UiCommandFields.POSITIVE_TEXT, config.defaultPositiveText());
        String negative = payload.optString(UiCommandFields.NEGATIVE_TEXT, config.defaultNegativeText());

        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onComplete(UiCommandProtocol.success(
                                commandJson,
                                result(true),
                                processName(activity),
                                elapsed(startMs)));
                    }
                })
                .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onComplete(UiCommandProtocol.success(
                                commandJson,
                                result(false),
                                processName(activity),
                                elapsed(startMs)));
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        callback.onComplete(UiCommandProtocol.success(
                                commandJson,
                                result(false),
                                processName(activity),
                                elapsed(startMs)));
                    }
                })
                .show();
    }

    private JSONObject result(boolean confirmed) {
        JSONObject data = new JSONObject();
        try {
            data.put(UiCommandFields.CONFIRMED, confirmed);
            data.put(UiCommandFields.PAGE_ID, pageId);
        } catch (JSONException ignored) {
        }
        return data;
    }

    private String processName(Activity activity) {
        Activity safeActivity = activity == null ? activityRef.get() : activity;
        return safeActivity == null ? "" : ProcessUtils.currentProcessName(safeActivity);
    }

    private long elapsed(long startMs) {
        return System.currentTimeMillis() - startMs;
    }
}
