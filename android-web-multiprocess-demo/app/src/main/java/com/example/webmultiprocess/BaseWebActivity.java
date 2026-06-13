package com.example.webmultiprocess;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.webmultiprocess.bridge.JsApiInvoker;
import com.example.webmultiprocess.bridge.WebJsBridge;
import com.example.webmultiprocess.ui.UiSessionRegistry;
import com.example.webmultiprocess.ui.WebUiSession;
import com.example.webmultiprocess.util.ProcessUtils;

public abstract class BaseWebActivity extends Activity {
    private WebView webView;
    private LinearLayout root;
    private String pageId;

    protected abstract JsApiInvoker createJsApiInvoker();

    protected abstract String modeLabel();

    protected abstract void onWebRendererGone(boolean didCrash);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageId = createPageId();
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        setContentView(root);
        addHeader();
        createWebView();
    }

    @Override
    protected void onDestroy() {
        UiSessionRegistry.unregister(pageId);
        if (webView != null) {
            webView.removeJavascriptInterface("NativeBridge");
            webView.stopLoading();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    private void addHeader() {
        TextView header = new TextView(this);
        header.setText(modeLabel() + "\n" + ProcessUtils.currentProcessName(this));
        header.setTextSize(13);
        header.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        header.setTextColor(0xFF1B2430);
        header.setPadding(dp(14), dp(10), dp(14), dp(10));
        header.setBackgroundColor(0xFFF7F9FC);
        root.addView(header, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void createWebView() {
        webView = new WebView(this);
        root.addView(webView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f));

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowContentAccess(false);
        settings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(false);
            settings.setAllowUniversalAccessFromFileURLs(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.setSafeBrowsingEnabled(true);
        }

        webView.removeJavascriptInterface("searchBoxJavaBridge_");
        webView.removeJavascriptInterface("accessibility");
        webView.removeJavascriptInterface("accessibilityTraversal");
        UiSessionRegistry.register(new WebUiSession(pageId, this));
        webView.addJavascriptInterface(
                new WebJsBridge(this, webView, createJsApiInvoker(), modeLabel(), pageId),
                "NativeBridge");
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new DemoWebViewClient());
        webView.loadUrl("file:///android_asset/web/demo.html?mode=" + Uri.encode(modeLabel()));
    }

    private String createPageId() {
        return "page_" + System.currentTimeMillis() + "_" + Integer.toHexString(System.identityHashCode(this));
    }

    private int dp(int value) {
        return Math.round(getResources().getDisplayMetrics().density * value);
    }

    private final class DemoWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            return !url.startsWith("file:///android_asset/") && !url.startsWith("https://");
        }

        @Override
        @TargetApi(Build.VERSION_CODES.O)
        public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
            if (root != null && webView != null) {
                root.removeView(webView);
                webView.destroy();
                webView = null;
            }
            onWebRendererGone(detail.didCrash());
            return true;
        }
    }
}
