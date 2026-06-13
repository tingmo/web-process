package com.example.webmultiprocess.jsapi;

public abstract class ConfiguredJsApiHandler implements JsApiHandler {
    private final JsApiContract.ApiSpec config;

    protected ConfiguredJsApiHandler(JsApiContract.ApiSpec config) {
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
