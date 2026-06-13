package com.example.webmultiprocess;

import android.app.Application;
import android.content.Context;

import com.example.webmultiprocess.ipc.MainProcessIPCService;
import com.example.webmultiprocess.ipc.WebProcessIPCService;
import com.example.webmultiprocess.jsapi.JsApiRegistry;

import cc.suitalk.ipcinvoker.IPCInvoker;
import cc.suitalk.ipcinvoker.activate.DefaultInitDelegate;
import cc.suitalk.ipcinvoker.activate.IPCInvokerInitializer;

public class DemoApplication extends Application {
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        JsApiRegistry.installDefaultHandlers();

        IPCInvoker.setup(this, new DefaultInitDelegate() {
            @Override
            public void onAttachServiceInfo(IPCInvokerInitializer initializer) {
                initializer.addIPCService(MainProcessIPCService.PROCESS_NAME, MainProcessIPCService.class);
                initializer.addIPCService(WebProcessIPCService.PROCESS_NAME, WebProcessIPCService.class);
            }
        });
    }

    public static Context getAppContext() {
        return appContext;
    }
}
