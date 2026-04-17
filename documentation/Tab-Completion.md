# ⌨️ Tab Completion

## 🌐 Overview

`command-lib` integrates with Paper async completion through `CommandTabCompleteManager`, which listens to `AsyncTabCompleteEvent`.

If the manager is created with:

```java
new CommandManager(this, true);
```

the built-in async completion listener is registered automatically.

## 🧠 Built-in Type Completions

The framework includes built-in completions for:

- `Player`
- `boolean`
- `Duration`
- enums
- `Enchantment`
- `ItemStack`

If a handler argument has a type-based completer, it is used automatically unless a method-level `@TabComplete` overrides that position.

## 🏷️ `@TabComplete` Syntax

`@TabComplete` accepts a space-separated declaration string. Each token maps to one argument position.

Supported token forms:

- `@id` for a completer registered by id
- `literal1|literal2|literal3` for inline literal values
- `*` to skip a position

Example:

```java
@SubCommand("gamemode")
@TabComplete("@players survival|creative|adventure|spectator")
public void gamemode(CommandSender sender, Player target, GameMode mode) {

}
```

## 🧾 Registering an Id-Based Completer

```java
manager.registerTabCompleter("@warps", sender -> List.of("spawn", "shop", "pvp"));
```

Then reference it:

```java
@SubCommand("teleport")
@TabComplete("@warps")
public void teleport(Player player, String warpName) {

}
```

## 🧾 Registering a Type-Based Completer

```java
manager.registerTabCompleter(Warp.class, (sender, argument) ->
        warpService.listNames()
);
```

This applies automatically to any `Warp` parameter.

## 🧪 Real Pattern

Larger plugins often use both type-based and id-based completion:

```java
commandManager.registerTabCompleter(Profile.class, (sender, argument) ->
        profileService.findAllNames());

commandManager.registerTabCompleter("@kits", sender ->
        kitService.listIds());
```

And then consumes that in a command:

```java
@SubCommand("give")
@TabComplete("* @kits")
public void giveExecutor(
        CommandSender sender,
        @Name("player") Player target,
        @Name("kit") String kitId,
        @Optional @Name("amount") Integer amount
) {

}
```

That is a strong real-world example of mixing:

- automatic player completion
- skipped first argument via `*`
- explicit id-based completion for the next argument

## 🧱 Literal Completion Example

```java
@SubCommand("visibility")
@TabComplete("public|private")
public void visibility(CommandSender sender, String visibility) {

}
```

## ⏭️ Skipping a Position

Use `*` when you want to leave one argument position to its existing type-based behavior or with no suggestions:

```java
@SubCommand("mail send")
@TabComplete("* @message-presets")
public void send(CommandSender sender, Player target, String preset) {

}
```

## 🧭 Subcommand Completion

The framework also suggests matching subcommand fragments based on the current input. This works for simple and multi-word subcommands.

Example subcommands:

- `teleport`
- `teleport here`
- `delete`

Typing `/warp te` will suggest matching subcommand segments.

## 🔐 Permission Filtering

Tab completion respects:

- command-level permission
- handler-level permission

If the sender cannot execute the handler, its completions are not exposed.

## 📌 Practical Notes

- tab completion support depends on Paper async command completion events
- the command must be registered through `CommandManager`
- completer output is returned as raw string suggestions
- custom managers are useful for larger projects with role-aware or config-aware completion behavior
