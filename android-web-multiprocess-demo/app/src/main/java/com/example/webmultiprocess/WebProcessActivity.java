package com.example.webmultiprocess;

import android.content.Context;
import android.content.Intent;

import com.example.webmultiprocess.bridge.JsApiInvoker;
import com.example.webmultiprocess.bridge.RemoteJsApiInvoker;

public class WebProcessActivity extends BaseWebActivity {
    public static Intent newIntent(Context context) {
        return new Intent(context, WebProcessActivity.class);
    }

    @Override
    protected JsApiInvoker createJsApiInvoker() {
        return new RemoteJsApiInvoker(this);
    }

    @Override
    protected String modeLabel() {
        return "multi_process:web_to_main";
    }

    @Override
    protected void onWebRendererGone(boolean didCrash) {
        startActivity(LocalWebActivity.newIntent(this, didCrash ? "renderer_crash" : "renderer_killed"));
        finish();
    }
}
