package com.example.webmultiprocess.ui;

import android.content.Context;

import com.example.webmultiprocess.ipc.InvokeUiCommandTask;
import com.example.webmultiprocess.ipc.WebProcessIPCService;
import com.example.webmultiprocess.util.ProcessUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import cc.suitalk.ipcinvoker.IPCInvokeCallback;
import cc.suitalk.ipcinvoker.IPCTask;

public final class UiCommandClient {
    private UiCommandClient() {
    }

    public static String dispatchSync(
            final Context context,
            String pageId,
            String command,
            String sourceApi,
            JSONObject payload,
            long timeoutMs) {
        final String commandJson = UiCommandProtocol.command(pageId, command, sourceApi, payload, timeoutMs);
        if (UiSessionRegistry.contains(pageId)) {
            return dispatchLocalSync(context, commandJson, timeoutMs);
        }
        return dispatchRemoteSync(context, commandJson, timeoutMs);
    }

    public static boolean isSuccess(String responseJson) {
        try {
            return new JSONObject(responseJson).optBoolean("success");
        } catch (JSONException ignored) {
            return false;
        }
    }

    public static JSONObject data(String responseJson) {
        try {
            JSONObject response = new JSONObject(responseJson);
            JSONObject data = response.optJSONObject("data");
            return data == null ? new JSONObject() : data;
        } catch (JSONException ignored) {
            return new JSONObject();
        }
    }

    public static String code(String responseJson) {
        try {
            return new JSONObject(responseJson).optString("code");
        } catch (JSONException ignored) {
            return "BAD_UI_COMMAND_RESPONSE";
        }
    }

    public static String message(String responseJson) {
        try {
            return new JSONObject(responseJson).optString("message");
        } catch (JSONException ignored) {
            return "Bad UI command response.";
        }
    }

    private static String dispatchLocalSync(
            final Context context,
            final String commandJson,
            long timeoutMs) {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<String> result = new AtomicReference<>();
        UiCommandDispatcher.dispatch(context, commandJson, new UiCommandCallback() {
            @Override
            public void onComplete(String responseJson) {
                result.set(responseJson);
                latch.countDown();
            }
        });
        await(latch, timeoutMs);
        String response = result.get();
        if (response != null) {
            return response;
        }
        return UiCommandProtocol.error(
                commandJson,
                "UI_COMMAND_TIMEOUT",
                "Local UI command timed out.",
                ProcessUtils.currentProcessName(context),
                timeoutMs);
    }

    private static String dispatchRemoteSync(
            final Context context,
            final String commandJson,
            long timeoutMs) {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<String> result = new AtomicReference<>();
        final String defaultResult = UiCommandProtocol.error(
                commandJson,
                "UI_COMMAND_TIMEOUT",
                "IPCInvoker UI command call timed out.",
                ProcessUtils.currentProcessName(context),
                timeoutMs);
        try {
            IPCTask.create(WebProcessIPCService.PROCESS_NAME)
                    .timeout(timeoutMs)
                    .async(InvokeUiCommandTask.class)
                    .data(commandJson)
                    .defaultResult(defaultResult)
                    .callback(false, new IPCInvokeCallback<String>() {
                        @Override
                        public void onCallback(String responseJson) {
                            result.set(responseJson);
                            latch.countDown();
                        }
                    })
                    .invoke();
        } catch (Throwable throwable) {
            return UiCommandProtocol.error(
                    commandJson,
                    "UI_COMMAND_IPC_EXCEPTION",
                    throwable.getClass().getSimpleName() + ": " + throwable.getMessage(),
                    ProcessUtils.currentProcessName(context),
                    0L);
        }
        await(latch, timeoutMs);
        String response = result.get();
        return response == null ? defaultResult : response;
    }

    private static void await(CountDownLatch latch, long timeoutMs) {
        try {
            latch.await(Math.max(100L, timeoutMs), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
