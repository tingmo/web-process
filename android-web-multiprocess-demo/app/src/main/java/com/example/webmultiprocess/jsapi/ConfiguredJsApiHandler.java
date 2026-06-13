package com.example.webmultiprocess.jsapi;

public abstract class ConfiguredJsApiHandler implements JsApiHandler {
    private final ApiConfig config;

    protected ConfiguredJsApiHandler(ApiConfig config) {
        this.config = config;
    }

    @Override
    public final String name() {
        return config.name();
    }

    @Override
    public final String version() {
        return config.version();
    }

    @Override
    public final String description() {
        return config.description();
    }

    @Override
    public final boolean mainProcessOnly() {
        return config.mainProcessOnly();
    }

    @Override
    public final boolean allowLocalFallback() {
        return config.allowLocalFallback();
    }
}
