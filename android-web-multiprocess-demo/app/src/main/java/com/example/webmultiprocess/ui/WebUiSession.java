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
    public void dispatchUiCommand(final String commandJson, final UiSession.Callback callback) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                dispatchOnMainThread(commandJson, callback);
            }
        });
    }

    private void dispatchOnMainThread(String commandJson, UiSession.Callback callback) {
        long startMs = System.currentTimeMillis();
        Activity activity = activityRef.get();
        if (activity == null || activity.isFinishing()) {
            callback.onComplete(UiCommandProtocol.error(
                    commandJson,
                    UiCommandContract.Code.ACTIVITY_UNAVAILABLE,
                    "The UI host Activity is gone.",
                    processName(activity),
                    elapsed(startMs)));
            return;
        }

        try {
            JSONObject request = new JSONObject(commandJson);
            String command = request.optString(UiCommandContract.Field.COMMAND);
            if (UiCommandContract.DIALOG_CONFIRM.command().equals(command)) {
                showConfirmDialog(activity, commandJson, request, callback, startMs);
                return;
            }
            callback.onComplete(UiCommandProtocol.error(
                    commandJson,
                    UiCommandContract.Code.COMMAND_NOT_FOUND,
                    "Unsupported UI command: " + command,
                    processName(activity),
                    elapsed(startMs)));
        } catch (JSONException e) {
            callback.onComplete(UiCommandProtocol.error(
                    commandJson,
                    UiCommandContract.Code.BAD_JSON,
                    e.getMessage(),
                    processName(activity),
                    elapsed(startMs)));
        }
    }

    private void showConfirmDialog(
            final Activity activity,
            final String commandJson,
            JSONObject request,
            final UiSession.Callback callback,
            final long startMs) {
        JSONObject payload = request.optJSONObject(UiCommandContract.Field.PAYLOAD);
        if (payload == null) {
            payload = new JSONObject();
        }
        UiCommandContract.CommandSpec config = UiCommandContract.DIALOG_CONFIRM;
        String title = payload.optString(UiCommandContract.Field.TITLE, config.defaultTitle());
        String message = payload.optString(UiCommandContract.Field.MESSAGE, config.defaultMessage());
        String positive = payload.optString(UiCommandContract.Field.POSITIVE_TEXT, config.defaultPositiveText());
        String negative = payload.optString(UiCommandContract.Field.NEGATIVE_TEXT, config.defaultNegativeText());

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
            data.put(UiCommandContract.Field.CONFIRMED, confirmed);
            data.put(UiCommandContract.Field.PAGE_ID, pageId);
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
