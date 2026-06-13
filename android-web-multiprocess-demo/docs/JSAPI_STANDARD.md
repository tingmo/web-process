# JSAPI Standard

Goal: JSAPI implementers should write business handlers only. They should not touch WebView process details, IPC binding, Binder lifecycle, or fallback behavior.

## Request

```json
{
  "callbackId": "cb_1710000000000_xxx",
  "api": "storage.get",
  "version": "1.0.0",
  "params": {
    "key": "demoKey"
  },
  "pageId": "page_1710000000000_abcd",
  "pageUrl": "file:///android_asset/web/demo.html",
  "bridgeVersion": "1.0.0",
  "timestamp": 1710000000000
}
```

Required fields:

- `callbackId`: async callback id.
- `api`: API name, recommended as `domain.action`.
- `params`: JSON object.
- `pageId`: live Web container id. Required for UICommand APIs.

## Response

```json
{
  "bridgeVersion": "1.0.0",
  "callbackId": "cb_1710000000000_xxx",
  "api": "storage.get",
  "success": true,
  "code": "OK",
  "message": "success",
  "data": {
    "found": true,
    "value": "hello"
  },
  "processMode": "main",
  "costMs": 2,
  "timestamp": 1710000000010
}
```

## Add A New API

1. Add one `JsApiContract.ApiSpec`.
2. Implement a handler extending `ConfiguredJsApiHandler`.
3. Register it in `JsApiRegistry.installDefaultHandlers()`.
4. Add Web demo config only if the API needs a test button.

Template:

```java
public class DemoEchoHandler extends ConfiguredJsApiHandler {
    public DemoEchoHandler() {
        super(JsApiContract.DEMO_ECHO);
    }

    @Override
    public JSONObject paramsSchema() {
        return JsonUtils.object("type", "object");
    }

    @Override
    public JSONObject resultSchema() {
        return JsonUtils.object("type", "object");
    }

    @Override
    public JsApiResult handle(JsApiContext context, JSONObject params) {
        return JsApiResult.success(JsonUtils.object("echo", params));
    }
}
```

## Rules

- API names, versions, process affinity, and fallback flags live in `JsApiContract`.
- Bridge fields and error codes live in `JsApiContract`.
- Handler code should contain business logic only.
- No `Activity`, `Fragment`, `WebView`, or `View` crosses IPC.
- Main-process APIs that need UI must use UICommand.
