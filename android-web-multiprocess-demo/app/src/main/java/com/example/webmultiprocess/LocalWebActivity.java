package com.example.webmultiprocess;

import android.content.Context;
import android.content.Intent;

import com.example.webmultiprocess.bridge.JsApiInvoker;
import com.example.webmultiprocess.bridge.LocalJsApiInvoker;

public class LocalWebActivity extends BaseWebActivity {
    private static final String EXTRA_REASON = "extra_reason";

    public static Intent newIntent(Context context, String reason) {
        Intent intent = new Intent(context, LocalWebActivity.class);
        intent.putExtra(EXTRA_REASON, reason);
        return intent;
    }

    @Override
    protected JsApiInvoker createJsApiInvoker() {
        return new LocalJsApiInvoker(this, false);
    }

    @Override
    protected String modeLabel() {
        String reason = getIntent().getStringExtra(EXTRA_REASON);
        return "local_degrade:" + (reason == null ? "unknown" : reason);
    }

    @Override
    protected void onWebRendererGone(boolean didCrash) {
        recreate();
    }
}
