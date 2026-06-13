# UICommand Design

UICommand solves one problem: a main-process JSAPI may need UI, but Android UI objects cannot cross processes.

## Principle

Cross process:

```text
pageId + command + payload
```

Stay local in UI process:

```text
Activity / Fragment / View / WebView
```

## Flow

```text
demo.confirmThenEcho
  -> UiCommandClient
  -> InvokeUiCommandTask in :web
  -> UiSessionRegistry.get(pageId)
  -> WebUiSession.dispatchUiCommand
  -> AlertDialog
  -> UICommand response
  -> JSAPI response
```

## Command Request

```json
{
  "commandId": "cmd_1710000000000",
  "pageId": "page_1710000000000_abcd",
  "command": "dialog.confirm",
  "sourceApi": "demo.confirmThenEcho",
  "payload": {
    "title": "UICommand from main process",
    "message": "Confirm this action?",
    "positiveText": "Confirm",
    "negativeText": "Cancel"
  },
  "timeoutMs": 6000,
  "timestamp": 1710000000000
}
```

## Command Response

```json
{
  "commandId": "cmd_1710000000000",
  "pageId": "page_1710000000000_abcd",
  "command": "dialog.confirm",
  "success": true,
  "code": "OK",
  "message": "success",
  "data": {
    "confirmed": true
  },
  "process": "com.example.webmultiprocess:web",
  "costMs": 1200,
  "timestamp": 1710000001200
}
```

## Add A New UICommand

1. Add one `UiCommandContract.CommandSpec`.
2. Add command dispatch logic in `WebUiSession`.
3. Call it from a main-process handler through `UiCommandClient`.

Handler side:

```java
UiCommandContract.CommandSpec command = UiCommandContract.DIALOG_CONFIRM;
String uiResponse = UiCommandClient.dispatchSync(
        context.getContext(),
        pageId,
        command.command(),
        name(),
        payload,
        command.timeoutMs());
```

## Failure Cases

- `UI_CONTEXT_UNAVAILABLE`: `pageId` has no live UI session.
- `ACTIVITY_UNAVAILABLE`: Activity is gone.
- `UI_COMMAND_NOT_FOUND`: UI process does not support the command.
- `UI_COMMAND_TIMEOUT`: no result before timeout.
