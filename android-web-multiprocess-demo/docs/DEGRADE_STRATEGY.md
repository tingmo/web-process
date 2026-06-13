# Degrade Strategy

## Default

Use `WebProcessActivity` in `:web`.

Benefits:

- WebView renderer and JS execution are isolated.
- Main process owns account, storage, and sensitive business state.
- JSAPI calls cross process through IPCInvoker.

## Local Fallback

Use `LocalWebActivity` when:

- `ActivityManager.isLowRamDevice()` is true.
- The `:web` Activity fails to start.
- WebView renderer crashes or is killed.
- Product config wants to disable multiprocess WebView.

## Per-API Fallback

When IPC to main process fails:

- If `allowLocalFallback=true`, execute the handler locally.
- If `allowLocalFallback=false`, return `PROCESS_UNAVAILABLE`.

Safe local fallback examples:

- `device.getInfo`
- `ui.toast`
- `runtime.getApiCatalog`
- `demo.echo`

Main-process-only examples:

- `user.getProfile`
- `storage.get`
- `storage.set`
- payment, permission, account, risk-control APIs

## Best Practice

- Keep process strategy in API specs, not in handlers.
- Never silently downgrade sensitive APIs.
- Add telemetry for `api`, `code`, `processMode`, `costMs`, and `pageId`.
- Use idempotency ids for APIs that may be retried.
- Use UICommand when a main-process handler needs UI.
