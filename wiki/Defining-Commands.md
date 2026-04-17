# 🧩 Defining Commands

## 🏷️ Command Class

Every command starts with a class that extends `CustomCommand`.

```java
@Command("example")
public final class ExampleCommand extends CustomCommand {

}
```

## 📌 Class-Level Annotations

### `@Command`

Declares the command name and aliases.

```java
@Command({"warp", "warps"})
public final class WarpCommand extends CustomCommand {

}
```

### `@Permission`

Declares a base permission for the whole command.

```java
@Command("warp")
@Permission("example.warp")
public final class WarpCommand extends CustomCommand {

}
```

If the sender lacks this permission, the framework stops before selecting a handler.

## 🛠️ Method-Level Annotations

### `@Default`

Defines the root handler for the command.

```java
@Default
public void defaultCommand(CommandSender sender) {
    sender.sendMessage("Use /warp teleport <player>");
}
```

### `@SubCommand`

Defines one or more subcommand labels handled by a method.

```java
@SubCommand("teleport")
public void teleport(CommandSender sender, Player target) {

}
```

Multiple values are allowed:

```java
@SubCommand({"delete", "remove"})
public void delete(CommandSender sender, String warpName) {

}
```

Multi-word subcommands are also supported:

```java
@SubCommand({"set public", "set private"})
public void setVisibility(CommandSender sender) {

}
```

### `@Permission` on a Handler

Adds a permission requirement only to that handler:

```java
@SubCommand("teleport")
@Permission("example.warp.teleport")
public void teleport(CommandSender sender, Player target) {

}
```

### `@Unknown`

Defines a fallback for unmatched subcommands:

```java
@Unknown(false)
public void unknown(CommandSender sender) {
    sender.sendMessage("Unknown subcommand.");
}
```

If `@Unknown(true)` is used, the same method is also exposed as a `help` subcommand.

```java
@Unknown(true)
public void help(CommandSender sender) {
    sender.sendMessage("/warp teleport <player>");
}
```

You can also protect that generated `help` subcommand with a specific permission:

```java
@Unknown(value = true, helpPermission = "example.warp.help")
public void help(CommandSender sender) {

}
```

## 📐 Handler Method Structure

A valid handler method follows this layout:

1. first parameter = sender type
2. remaining parameters = parsed command arguments

Supported sender types:

- `CommandSender`
- `Player`
- `ConsoleCommandSender`

Any other first parameter causes a runtime failure during command setup.

## 🧠 Real Usage Patterns

In larger plugins, command sets often use patterns like:

- player-only subcommands
- optional trailing arguments
- explicit manual help through `@Unknown(false)`
- class-level base permissions with method-level special permissions

## 🧪 Full Example

```java
@Command("mail")
@Permission("example.mail")
public final class MailCommand extends CustomCommand {

    @Default
    public void defaultCommand(CommandSender sender) {
        sender.sendMessage("/mail send <player> <message>");
    }

    @SubCommand("send")
    @Permission("example.mail.send")
    public void send(
            Player sender,
            @Name("player") Player target,
            @Join @Name("message") String message
    ) {
        target.sendMessage(sender.getName() + ": " + message);
    }

    @Unknown(true)
    public void unknown(CommandSender sender) {
        sender.sendMessage("Unknown subcommand.");
    }

}
```
