# 🚀 Getting Started

This page shows the smallest practical setup to get `command-lib` working inside a Paper plugin.

## 1. Create a Command Manager

```java
CommandManager manager = new CommandManager(this, true);
```

This manager becomes the central registry for:

- commands
- argument resolvers
- tab completers

## 2. Create a Command Class

Extend `CustomCommand` and annotate the class with `@Command`.

```java
@Command({"warp", "warps"})
@Permission("example.warp")
public final class WarpCommand extends CustomCommand {

}
```

The first command name is the primary label. The remaining values are aliases.

## 3. Add Handlers

Use method annotations to define command behavior.

```java
@Command("warp")
public final class WarpCommand extends CustomCommand {

    @Default
    public void defaultCommand(CommandSender sender) {
        sender.sendMessage("Use /warp teleport <player>");
    }

    @SubCommand("teleport")
    public void teleport(CommandSender sender, Player target) {
        sender.sendMessage("Teleporting to " + target.getName());
    }

    @Unknown(true)
    public void unknown(CommandSender sender) {
        sender.sendMessage("Unknown subcommand.");
    }

}
```

## 4. Register the Command

```java
manager.registerCommand(new WarpCommand());
```

The manager reads the command metadata from annotations on the class and methods.

## 👤 Sender Types

The first parameter of every handler method defines who may execute it.

- `CommandSender` for players and console
- `Player` for players only
- `ConsoleCommandSender` for console only

Examples:

```java
@Default
public void playerOnly(Player player) {

}
```

```java
@SubCommand("reload")
public void consoleOnly(ConsoleCommandSender sender) {

}
```

## 🧭 Dispatch Order

When a command runs, the framework checks handlers in this order:

1. matching `@SubCommand`
2. matching `@Default`
3. fallback `@Unknown`

If nothing matches and no unknown handler exists, execution ends in a runtime failure. In practice, defining `@Unknown`, `@Default`, or both is the safest pattern.

## 🏗️ Real Project Pattern

In larger plugins, a common pattern is to disable the default listener and install a custom tab completion manager:

```java
CommandManager commandManager = new CommandManager(this, false);
commandManager.setTabCompleteManager(new CustomTabCompleteManager(commandManager));
```

That pattern is useful when your project needs a custom tab completion layer on top of the default framework behavior.

## ✅ Minimal Recommended Pattern

```java
@Command("example")
public final class ExampleCommand extends CustomCommand {

    @Default
    public void defaultCommand(CommandSender sender) {
        sender.sendMessage("/example help");
    }

    @Unknown(true)
    public void unknown(CommandSender sender) {
        sender.sendMessage("Unknown subcommand.");
    }

}
```
