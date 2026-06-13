package com.example.webmultiprocess;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.webmultiprocess.jsapi.JsApiHandler;
import com.example.webmultiprocess.jsapi.JsApiRegistry;
import com.example.webmultiprocess.util.ProcessUtils;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        setContentView(createContentView());
    }

    private View createContentView() {
        ScrollView scrollView = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(20), dp(20), dp(20));
        scrollView.addView(root);

        root.addView(text("Android Web Multiprocess Demo", 24, true));

        String mode = ProcessModeChooser.shouldUseMultiProcess(this)
                ? "Recommended: :web process"
                : "Recommended: local fallback";
        TextView status = text(
                "Current process: " + ProcessUtils.currentProcessName(this) + "\n" + mode,
                14,
                false);
        status.setPadding(0, dp(8), 0, dp(18));
        root.addView(status);

        Button recommended = button("Open Web by strategy");
        recommended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ProcessModeChooser.shouldUseMultiProcess(MainActivity.this)) {
                    openWebProcess();
                } else {
                    openLocal(ProcessModeChooser.localModeReason(MainActivity.this));
                }
            }
        });
        root.addView(recommended);

        Button multi = button("Force :web process");
        multi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebProcess();
            }
        });
        root.addView(multi);

        Button local = button("Open local fallback");
        local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLocal("manual");
            }
        });
        root.addView(local);

        TextView section = text("Registered JSAPI", 18, true);
        section.setPadding(0, dp(24), 0, dp(10));
        root.addView(section);

        for (JsApiHandler handler : JsApiRegistry.list()) {
            TextView item = text(
                    handler.name()
                            + "  v" + handler.version()
                            + "\nmainOnly=" + handler.mainProcessOnly()
                            + ", localFallback=" + handler.allowLocalFallback()
                            + "\n" + handler.description(),
                    13,
                    false);
            item.setPadding(dp(12), dp(10), dp(12), dp(10));
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, dp(8));
            item.setLayoutParams(params);
            item.setBackgroundColor(0xFFF2F5F9);
            root.addView(item);
        }

        return scrollView;
    }

    private void openWebProcess() {
        try {
            startActivity(WebProcessActivity.newIntent(this));
        } catch (ActivityNotFoundException e) {
            openLocal("web_process_activity_missing");
        } catch (RuntimeException e) {
            openLocal("web_process_start_failed");
        }
    }

    private void openLocal(String reason) {
        startActivity(LocalWebActivity.newIntent(this, reason));
    }

    private TextView text(String value, int sp, boolean bold) {
        TextView textView = new TextView(this);
        textView.setText(value);
        textView.setTextSize(sp);
        textView.setTextColor(0xFF18202A);
        textView.setLineSpacing(0f, 1.12f);
        if (bold) {
            textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }
        return textView;
    }

    private Button button(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48));
        params.setMargins(0, 0, 0, dp(10));
        button.setLayoutParams(params);
        return button;
    }

    private int dp(int value) {
        return Math.round(getResources().getDisplayMetrics().density * value);
    }
}
