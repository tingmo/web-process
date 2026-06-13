# JSAPI 标准

目标：后续 JSAPI 实现方只关心业务处理，不关心 WebView 进程、IPCInvoker、Binder 生命周期和降级细节。

## Web 请求格式

```json
{
  "callbackId": "cb_1710000000000_xxx",
  "api": "storage.get",
  "version": "1.0.0",
  "params": {
    "key": "demoKey"
  },
  "pageUrl": "file:///android_asset/web/demo.html",
  "bridgeVersion": "1.0.0",
  "timestamp": 1710000000000
}
```

字段约定：

- `callbackId`: 必填，用于异步响应匹配。
- `api`: 必填，命名建议为 `domain.action`，例如 `user.getProfile`。
- `version`: 接口版本，默认 `1.0.0`。
- `params`: 业务参数，必须是 JSON object。
- `pageUrl`: 调用来源，Native 可用于鉴权、审计、灰度。
- `bridgeVersion`: JSBridge 协议版本。
- `timestamp`: Web 发起时间。

## Native 响应格式

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

错误码建议：

- `BAD_JSON`: 请求不是合法 JSON。
- `INVALID_CALLBACK`: 缺少 `callbackId`。
- `INVALID_API`: 缺少 `api`。
- `API_NOT_FOUND`: 未注册接口。
- `INVALID_PARAM`: 参数错误。
- `HANDLER_EXCEPTION`: 实现抛出异常。
- `IPC_TIMEOUT`: IPCInvoker 调用主进程超时。
- `IPC_EXCEPTION`: IPCInvoker 调用异常。
- `PROCESS_UNAVAILABLE`: 主进程不可用且该接口不允许本地降级。

## 实现方模板

新增接口先声明配置，再实现 handler。

配置位置：

```java
public static final ApiConfig DEMO_ECHO = new ApiConfig(
        "demo.echo",
        VERSION_1,
        "A standard handler template for future JSAPI implementers.",
        false,
        true);
```

Handler 继承 `ConfiguredJsApiHandler`：

```java
public class DemoEchoHandler extends ConfiguredJsApiHandler {
    public DemoEchoHandler() {
        super(ApiConfigs.DEMO_ECHO);
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

注册位置：

```java
JsApiRegistry.register(new DemoEchoHandler());
```

demo 默认在 `JsApiRegistry.installDefaultHandlers()` 中注册示例接口。

约束：

- 不在 handler 里硬编码 API 名、版本、描述和进程策略。
- 请求/响应字段使用 `BridgeFields`。
- 错误码使用 `BridgeCodes`。
- 常用参数名放入类似 `DemoParams` 的参数常量类。

## 接口分层建议

- Web 层只依赖 `window.WebNative.invoke(api, params)`。
- Bridge 层只做 JSON 入站、回调出站和超时保护。
- IPC 层只做跨进程转发，不放业务逻辑。
- Dispatcher 层统一做参数校验、错误包装、耗时统计。
- Handler 层只放单个 JSAPI 的业务实现。
- 需要 UI 的主进程 Handler 只能通过 UICommand 请求 UI 进程，不直接依赖 Activity/Fragment。

## 标准化展示

每个 Handler 必须提供：

- `name`
- `version`
- `description`
- `mainProcessOnly`
- `allowLocalFallback`
- `paramsSchema`
- `resultSchema`

这些字段会被 `runtime.getApiCatalog` 输出给 Web，用于生成调试页、接口文档、灰度平台或自动化测试用例。

## UI 相关接口

如果接口需要 `Activity`、`Fragment`、`View`、`WebView`：

- 纯 UI 接口留在 Web/UI 进程执行。
- 主进程业务接口通过 `UiCommandClient` 发送 `pageId + command + payload`。
- UI 进程通过 `UiSessionRegistry` 查找页面宿主。
- 找不到页面宿主时返回 `UI_CONTEXT_UNAVAILABLE`。

完整设计见 [UICOMMAND_DESIGN.md](UICOMMAND_DESIGN.md)。
