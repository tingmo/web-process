# Android Web Multiprocess Demo

Minimal demo for a WebView container with:

- WebView isolated in `:web`.
- JSAPI business handlers executed in the main process when needed.
- IPCInvoker as the only cross-process transport.
- UICommand for main-process handlers that need UI actions.
- Local fallback for safe APIs when IPC is unavailable.

## Run

Open `android-web-multiprocess-demo` in Android Studio and run `app`.

Dependency:

```gradle
implementation "cc.suitalk.android:ipc-invoker:1.3.7"
```

The demo also adds the public repository that hosts this artifact:

```gradle
maven {
    url "https://verve.jfrog.io/artifactory/verve-gradle-release/"
}
```

## Modules

```text
app/
  DemoApplication        IPCInvoker setup and JSAPI registration
  MainActivity           Launcher page and demo entry
  WebProcessActivity     WebView container in :web
  LocalWebActivity       Main-process fallback WebView container
  bridge/                JS <-> Native bridge and remote/local invokers
  ipc/                   IPCInvoker services and tasks
  jsapi/                 JSAPI contract, dispatcher, registry, result model
  jsapi/handlers/        Example JSAPI handlers
  ui/                    UICommand contract, session registry, UI dispatcher
  assets/web/demo.html   Web-side invoke wrapper and test page
```

## Main Flow

```text
Web JS
  -> NativeBridge.postMessage(requestJson)
  -> WebJsBridge
  -> RemoteJsApiInvoker
  -> IPCInvoker InvokeJsApiTask
  -> JsApiDispatcher
  -> JsApiHandler
  -> response JSON
  -> window.WebNative.__callback(responseJson)
```

Only JSON crosses processes. `Activity`, `Fragment`, `WebView`, `View`, and old `Bridge` objects stay in the local UI process.

## UICommand Flow

Used when a main-process JSAPI needs UI:

```text
Main-process JsApiHandler
  -> UiCommandClient(pageId, command, payload)
  -> IPCInvoker InvokeUiCommandTask
  -> UiSessionRegistry.lookup(pageId)
  -> WebUiSession
  -> Activity / Dialog / Fragment action
  -> UICommand response JSON
  -> JsApiHandler result
```

## Key Contracts

- `JsApiContract`: API specs, bridge fields, bridge error codes, demo constants.
- `UiCommandContract`: UI command specs, UI command fields, UI command error codes.
- `ConfiguredJsApiHandler`: removes duplicated `name/version/description/process` boilerplate.
- `JsApiDispatcher`: validates the request and calls the registered handler.
- `UiSessionRegistry`: maps `pageId` to a live UI session by weak reference.

## Example APIs

| API | Purpose | Main only | Local fallback |
| --- | --- | --- | --- |
| `runtime.getApiCatalog` | Expose handler catalog | No | Yes |
| `device.getInfo` | Device/process info | No | Yes |
| `ui.toast` | Native toast | No | Yes |
| `user.getProfile` | Mock main-process account data | Yes | No |
| `storage.set` | Main-process storage write | Yes | No |
| `storage.get` | Main-process storage read | Yes | No |
| `demo.echo` | Handler template | No | Yes |
| `demo.confirmThenEcho` | Main handler asks UI process to confirm | Yes | No |

More details:

- [JSAPI_STANDARD.md](docs/JSAPI_STANDARD.md)
- [UICOMMAND_DESIGN.md](docs/UICOMMAND_DESIGN.md)
- [DEGRADE_STRATEGY.md](docs/DEGRADE_STRATEGY.md)
