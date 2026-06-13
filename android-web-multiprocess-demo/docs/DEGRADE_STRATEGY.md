# 多进程与降级策略

## 启动策略

默认推荐 `:web` 子进程：

- WebView 页面、JS 执行、渲染风险隔离在 `:web`。
- 主进程保留账号态、存储、支付、权限等敏感能力。
- JSAPI 通过 IPCInvoker 从 `:web` 转发到主进程。

以下场景进入主进程本地降级：

- `ActivityManager.isLowRamDevice()` 返回 true。
- `WebProcessActivity` 启动失败。
- WebView renderer crash 或被系统 kill。
- 产品灰度或开关要求强制本地模式。

## JSAPI 调用降级

`RemoteJsApiInvoker` 调用主进程时会设置 IPCInvoker timeout 和 default result。

当出现 `IPC_TIMEOUT`、`IPC_NO_RESULT`、`IPC_EXCEPTION` 时：

- 如果接口 `allowLocalFallback=true`，转给 `LocalJsApiInvoker` 在 Web 进程本地执行。
- 如果接口 `allowLocalFallback=false`，返回 `PROCESS_UNAVAILABLE`。

这可以保护账号、支付、持久化等主进程限定能力不会在 Web 子进程随意降级。

## 接口分级

| 类型 | 示例 | mainProcessOnly | allowLocalFallback |
| --- | --- | --- | --- |
| 纯查询、无敏感数据 | `device.getInfo` | false | true |
| UI 弱依赖 | `ui.toast` | false | true |
| 调试/目录 | `runtime.getApiCatalog` | false | true |
| 账号态 | `user.getProfile` | true | false |
| 主进程存储 | `storage.get` / `storage.set` | true | false |
| 主进程业务 + UI 动作 | `demo.confirmThenEcho` | true | false |
| 支付、权限、风控 | 后续业务接口 | true | false |

## 进程恢复

当前 demo 做法：

- WebView renderer 异常后销毁旧 WebView。
- `:web` 模式异常时启动 `LocalWebActivity` 并结束当前页面。
- 本地模式 renderer 异常时调用 `recreate()` 重建页面。

生产建议：

- 增加远端配置开关控制多进程命中率。
- 记录 `processMode`、`code`、`api`、`costMs`、页面 URL 到监控系统。
- 对高风险 API 加调用白名单和来源校验。
- 为重要 API 增加幂等 id，避免 IPC 重试导致重复执行。
- 对 Web 子进程内存、ANR、renderer crash 做单独埋点。
- 主进程需要 UI 时走 UICommand；不要缓存或跨进程传递 Activity/Fragment/View。
