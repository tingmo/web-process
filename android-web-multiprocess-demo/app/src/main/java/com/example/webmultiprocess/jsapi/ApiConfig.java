package com.example.webmultiprocess.jsapi;

public final class ApiConfig {
    private final String name;
    private final String version;
    private final String description;
    private final boolean mainProcessOnly;
    private final boolean allowLocalFallback;

    public ApiConfig(
            String name,
            String version,
            String description,
            boolean mainProcessOnly,
            boolean allowLocalFallback) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.mainProcessOnly = mainProcessOnly;
        this.allowLocalFallback = allowLocalFallback;
    }

    public String name() {
        return name;
    }

    public String version() {
        return version;
    }

    public String description() {
        return description;
    }

    public boolean mainProcessOnly() {
        return mainProcessOnly;
    }

    public boolean allowLocalFallback() {
        return allowLocalFallback;
    }
}
