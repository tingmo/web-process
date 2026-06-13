package com.example.webmultiprocess.jsapi;

import com.example.webmultiprocess.jsapi.handlers.DemoEchoHandler;
import com.example.webmultiprocess.jsapi.handlers.ConfirmThenEchoHandler;
import com.example.webmultiprocess.jsapi.handlers.DeviceInfoHandler;
import com.example.webmultiprocess.jsapi.handlers.RuntimeCatalogHandler;
import com.example.webmultiprocess.jsapi.handlers.StorageGetHandler;
import com.example.webmultiprocess.jsapi.handlers.StorageSetHandler;
import com.example.webmultiprocess.jsapi.handlers.ToastHandler;
import com.example.webmultiprocess.jsapi.handlers.UserProfileHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JsApiRegistry {
    private static final Map<String, JsApiHandler> HANDLERS = new LinkedHashMap<>();
    private static boolean installed;

    private JsApiRegistry() {
    }

    public static synchronized void installDefaultHandlers() {
        if (installed) {
            return;
        }
        register(new RuntimeCatalogHandler());
        register(new DeviceInfoHandler());
        register(new ToastHandler());
        register(new StorageSetHandler());
        register(new StorageGetHandler());
        register(new UserProfileHandler());
        register(new DemoEchoHandler());
        register(new ConfirmThenEchoHandler());
        installed = true;
    }

    public static synchronized void register(JsApiHandler handler) {
        HANDLERS.put(handler.name(), handler);
    }

    public static synchronized JsApiHandler get(String name) {
        return HANDLERS.get(name);
    }

    public static synchronized boolean allowLocalFallback(String name) {
        JsApiHandler handler = HANDLERS.get(name);
        return handler != null && handler.allowLocalFallback();
    }

    public static synchronized List<JsApiHandler> list() {
        return new ArrayList<>(HANDLERS.values());
    }

    public static synchronized JSONArray catalogJson() {
        JSONArray array = new JSONArray();
        for (JsApiHandler handler : HANDLERS.values()) {
            JSONObject item = new JSONObject();
            try {
                item.put(ApiCatalogFields.NAME, handler.name());
                item.put(ApiCatalogFields.VERSION, handler.version());
                item.put(ApiCatalogFields.DESCRIPTION, handler.description());
                item.put(ApiCatalogFields.MAIN_PROCESS_ONLY, handler.mainProcessOnly());
                item.put(ApiCatalogFields.ALLOW_LOCAL_FALLBACK, handler.allowLocalFallback());
                item.put(ApiCatalogFields.PARAMS_SCHEMA, handler.paramsSchema());
                item.put(ApiCatalogFields.RESULT_SCHEMA, handler.resultSchema());
            } catch (JSONException ignored) {
            }
            array.put(item);
        }
        return array;
    }
}
