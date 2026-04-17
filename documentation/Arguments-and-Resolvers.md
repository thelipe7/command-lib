# 🔎 Arguments and Resolvers

## 🧰 Built-in Argument Support

Out of the box, the framework includes resolvers for:

- `String`
- `short` / `Short`
- `int` / `Integer`
- `long` / `Long`
- `float` / `Float`
- `double` / `Double`
- `boolean` / `Boolean`
- `Duration`
- `Player`
- enums
- `Enchantment`
- `ItemStack`
- `GameMode`

If a parameter type has no resolver and is not `String`, execution fails with a runtime exception.

## 🏷️ `@Name`

Overrides the argument name used in generated usage messages.

```java
@SubCommand("teleport")
public void teleport(CommandSender sender, @Name("player") Player target) {

}
```

## ➕ `@Optional`

Marks an argument as optional.

```java
@SubCommand("give")
public void give(CommandSender sender, Player target, @Optional Integer amount) {

}
```

When omitted, the framework passes `null` for nullable types.

Recommended usage:

- place `@Optional` on the final argument
- prefer wrapper types such as `Integer` instead of primitives

Real-world example:

```java
@SubCommand("give")
@TabComplete("* @items")
public void giveExecutor(
        CommandSender sender,
        @Name("player") Player target,
        @Name("item") int itemId,
        @Optional @Name("amount") Integer amount
) {
    if (amount == null || amount <= 0) amount = 1;
}
```

## 🔗 `@Join`

Consumes the remaining raw input as one string.

```java
@SubCommand("broadcast")
public void broadcast(CommandSender sender, @Join @Name("message") String message) {
    sender.getServer().broadcastMessage(message);
}
```

Recommended usage:

- place `@Join` on the final argument
- use it with `String`

## 🔢 Primitive Parsing

Primitive and wrapper numbers are parsed through standard Java parsing methods.

Examples:

- `int` uses `Integer.parseInt(...)`
- `double` uses `Double.parseDouble(...)`

If parsing fails, the framework sends the default invalid value message and stops execution.

## ✅ Boolean Parsing

`boolean` and `Boolean` are parsed through `Boolean.valueOf(...)`.

That means:

- `true` becomes `true`
- any other non-null value becomes `false`

If you need stricter boolean validation, register your own resolver.

## ⏱️ Duration Format

`Duration` supports the format `1d2h3m4s`.

Examples:

- `10s`
- `1m30s`
- `30m`
- `1h45m`
- `1d3h15m`

## 🧩 Enum Resolution

Enums are resolved by constant name and receive built-in tab completion automatically.

```java
public enum Visibility {
    PUBLIC,
    PRIVATE
}
```

```java
@SubCommand("visibility")
public void visibility(CommandSender sender, Visibility visibility) {

}
```

## 👤 Player Resolution

`Player` arguments resolve through `Bukkit.getPlayer(input)`.

This means:

- only online players are matched
- unmatched players trigger `MessageProvider.playerNotFound(...)`

## 🎒 `ItemStack` Resolution

`ItemStack` supports:

- material name only, such as `DIAMOND`
- material with data syntax, such as `WOOL:5`

The parser uses Bukkit `Material` enum names.

## 🎮 `GameMode` Resolution

`GameMode` accepts:

- enum names like `SURVIVAL`
- numeric legacy ids `0`, `1`, `2`, `3`

## 🛠️ Custom Argument Resolvers

Register a parser for your own type:

```java
manager.registerArgumentResolver(Warp.class, (sender, input, argument) -> {
    Warp warp = warpService.findByName(input);
    if (warp == null) {
        sender.sendMessage("Warp not found.");
        return new ArgumentResult<>(null, ResultStatus.FAIL);
    }

    return new ArgumentResult<>(warp, ResultStatus.SUCCESS);
});
```

Then use the type directly:

```java
@SubCommand("teleport")
public void teleport(Player player, Warp warp) {
    player.teleport(warp.location());
}
```

## 🏗️ Real Resolver Setup

In larger plugins, it is common to register resolvers for domain-specific types such as:

- user or account objects
- permission or role objects
- region objects
- flag objects
- system or module objects
- custom content objects

That is the intended extension model of the library: plugin-specific domain types should be mapped once in your startup registry, then used directly in handler methods.

## 📜 Resolver Contract

A custom resolver should:

- return `ResultStatus.SUCCESS` on success
- return `ResultStatus.FAIL` on failure
- send any user-facing message before returning `FAIL`

When a resolver returns `FAIL`, command execution stops immediately.
