# 🛠️ Troubleshooting

## `Command annotation not found`

### Cause

The command class was registered without a class-level `@Command`.

### Fix

```java
@Command("example")
public final class ExampleCommand extends CustomCommand {

}
```

## `Invalid sender`

### Cause

The first parameter of a handler method is not a supported sender type.

### Fix

Use one of:

- `CommandSender`
- `Player`
- `ConsoleCommandSender`

## `No argument resolver found`

### Cause

The framework cannot parse a parameter type used by a handler.

### Fix

Register a custom resolver:

```java
manager.registerArgumentResolver(MyType.class, (sender, input, argument) -> {
    MyType value = service.find(input);
    if (value == null) {
        sender.sendMessage("Not found.");
        return new ArgumentResult<>(null, ResultStatus.FAIL);
    }

    return new ArgumentResult<>(value, ResultStatus.SUCCESS);
});
```

## Invalid Usage Output

### Cause

Generated usage uses parameter names unless you override them with `@Name`.

### Fix

```java
public void teleport(CommandSender sender, @Name("player") Player target) {

}
```

## Optional Argument Null Issues

### Cause

Omitted `@Optional` arguments are passed as `null`.

### Fix

- use wrapper types like `Integer`
- null-check before use

Example:

```java
public void give(CommandSender sender, Player target, @Optional Integer amount) {
    int finalAmount = amount != null ? amount : 1;
}
```

## Joined Argument not Capturing Full Text

### Cause

`@Join` is intended to consume the remaining raw input.

### Fix

Use it on the final `String` argument:

```java
public void broadcast(CommandSender sender, @Join @Name("message") String message) {
}
```

## Tab Completion not Working

Check the following:

- the server is Paper
- the command was registered through `CommandManager`
- `CommandManager` was created with `true`, or a custom `CommandTabCompleteManager` was installed
- the sender has permission for the command or subcommand
- the `@TabComplete` declaration matches the argument positions you expect

## Unknown Subcommands are not Friendly

### Fix

Add an unknown handler:

```java
@Unknown(true)
public void unknown(CommandSender sender) {
    sender.sendMessage("Unknown subcommand.");
}
```

## Real-World Advice

If your project has:

- project-specific permissions
- dynamic completions from configs or roles
- domain objects such as `Region`, `Account`, or `Frame`

then the right pattern is:

1. register custom resolvers at startup
2. register custom tab completers at startup
3. keep command handlers focused on command behavior, not on manual string parsing
