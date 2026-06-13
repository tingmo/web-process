package com.example.webmultiprocess.ui;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class UiSessionRegistry {
    private static final Map<String, WeakReference<UiSession>> SESSIONS = new ConcurrentHashMap<>();

    private UiSessionRegistry() {
    }

    public static void register(UiSession session) {
        if (session == null || session.getPageId() == null || session.getPageId().length() == 0) {
            return;
        }
        SESSIONS.put(session.getPageId(), new WeakReference<>(session));
    }

    public static void unregister(String pageId) {
        if (pageId != null) {
            SESSIONS.remove(pageId);
        }
    }

    public static UiSession get(String pageId) {
        WeakReference<UiSession> reference = SESSIONS.get(pageId);
        UiSession session = reference == null ? null : reference.get();
        if (session == null && pageId != null) {
            SESSIONS.remove(pageId);
        }
        return session;
    }

    public static boolean contains(String pageId) {
        return get(pageId) != null;
    }
}
