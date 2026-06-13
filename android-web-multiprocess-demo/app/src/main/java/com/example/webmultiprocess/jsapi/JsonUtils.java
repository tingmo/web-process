package com.example.webmultiprocess.jsapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class JsonUtils {
    private JsonUtils() {
    }

    public static JSONObject object(Object... keyValues) {
        JSONObject json = new JSONObject();
        for (int i = 0; i + 1 < keyValues.length; i += 2) {
            try {
                json.put(String.valueOf(keyValues[i]), keyValues[i + 1]);
            } catch (JSONException ignored) {
            }
        }
        return json;
    }

    public static JSONArray array(Object... values) {
        JSONArray array = new JSONArray();
        for (Object value : values) {
            array.put(value);
        }
        return array;
    }
}
