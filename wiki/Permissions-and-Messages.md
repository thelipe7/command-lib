# 🔐 Permissions and Messages

## 🧱 Permission Layers

The framework supports two permission layers.

### Command-Level Permission

Applied at class level:

```java
@Command("warp")
@Permission("example.warp")
public final class WarpCommand extends CustomCommand {

}
```

This is checked before handler dispatch.

### Handler-Level Permission

Applied to a specific handler:

```java
@SubCommand("delete")
@Permission("example.warp.delete")
public void delete(CommandSender sender, String warpName) {

}
```

This is checked after the framework resolves the matching handler.

## 💬 Message Provider

User-facing framework messages are centralized in `MessageProvider`.

The default provider defines methods for:

- `noPermission`
- `invalidUsage`
- `playerNotFound`
- `onlyPlayer`
- `onlyConsole`
- `invalidValue`
- `invalidDuration`

## 🔁 Replacing the Provider

Set a new provider during startup:

```java
MessageProvider.setInstance(new MessageProvider() {
    @Override
    public void noPermission(CommandSender sender) {
        sender.sendMessage("You do not have permission.");
    }

    @Override
    public void invalidUsage(CommandSender sender, String usage) {
        sender.sendMessage("Usage: /" + usage);
    }
});
```

## 🎨 MiniMessage Support

`CommandUtil.sendMessage(...)` uses Adventure MiniMessage under the hood:

```java
CommandUtil.sendMessage(sender, "<red>Invalid value.");
```

That makes it easy to keep rich formatting consistent across command messages.

## 🏗️ Suggested Pattern

Create a dedicated provider class for your plugin:

```java
public final class MyMessageProvider extends MessageProvider {

    @Override
    public void noPermission(CommandSender sender) {
        CommandUtil.sendMessage(sender, "<red>You do not have permission.");
    }

}
```

Then register it once:

```java
@Override
public void onEnable() {
    MessageProvider.setInstance(new MyMessageProvider());
}
```

## 🧾 Usage Generation

When the framework detects invalid input, it automatically generates usage text from handler parameter names and annotations.

Examples:

- required arguments become `<name>`
- optional arguments become `[name]`

Using `@Name` makes usage output cleaner and more stable than relying on compiled parameter names.

## 🧠 Real Usage Pattern

In larger plugins, many commands use:

- class-level base permission for the whole command
- manual help output in `@Unknown(false)`
- domain-specific message systems layered on top of the framework

That is a good pattern when you want project-specific UX without losing the framework's dispatch and parsing behavior.
