# 🧪 API Reference

## 🧱 Core Types

### `CommandManager`

Central registry for:

- commands
- argument resolvers
- tab completers
- the optional tab completion listener

Main methods:

```java
CommandManager(JavaPlugin plugin, boolean autoRegisterTabCompleteManager)
RegisteredCommand registerCommand(CustomCommand customCommand)
RegisteredCommand registerCommand(CustomCommand customCommand, String permission, String... names)
void unregisterCommand(RegisteredCommand registeredCommand)
<T> void registerArgumentResolver(Class<T> clazz, ArgumentResolver<T> argumentResolver)
void unregisterArgumentResolver(Class<?> clazz)
void registerTabCompleter(String id, Function<CommandSender, Collection<String>> resolver)
void registerTabCompleter(Class<?> clazz, BiFunction<CommandSender, Argument, Collection<String>> resolver)
void unregisterTabCompleter(String id)
void unregisterTabCompleter(Class<?> clazz)
void setTabCompleteManager(CommandTabCompleteManager tabCompleteManager)
```

### `CustomCommand`

Base class for all annotation-driven command classes.

Helper method:

```java
protected void sendUseMessage(CommandSender sender, String usage)
```

### `RegisteredCommand`

Runtime wrapper around a command definition. Extends Bukkit's `org.bukkit.command.Command` and manages:

- the primary name and aliases
- the base permission
- the default executor
- the unknown executor
- all subcommand executors

### `CommandExecutor`

Runtime representation of a single command handler method. It stores:

- method metadata
- sender restriction
- parsed argument metadata
- permission requirement
- tab completion mapping

### `CommandTabCompleteManager`

Listener that plugs into Paper async tab completion and resolves suggestions for commands managed by the framework.

## 🏷️ Annotations

### `@Command`

Class-level command name and aliases.

### `@Permission`

Class-level or method-level permission requirement.

### `@Default`

Default handler for the base command.

### `@SubCommand`

Subcommand handler annotation. Supports aliases and multi-word labels.

### `@Unknown`

Fallback handler for unmatched subcommands, with optional generated `help` subcommand support.

### `@TabComplete`

Explicit tab completion declaration for handler arguments.

### `@Name`

Custom argument display name for usage messages.

### `@Optional`

Marks an optional argument.

### `@Join`

Marks an argument that consumes the remaining raw input as one string.

## 🧩 Argument API

### `Argument`

Metadata object describing one parsed command parameter.

Fields:

- `name`
- `clazz`
- `position`
- `optional`
- `join`

### `ArgumentResolver<T>`

Functional interface:

```java
ArgumentResult<T> resolve(CommandSender sender, String input, Argument argument)
```

### `ArgumentResult<T>`

Resolver return object containing:

- `result`
- `status`

### `ResultStatus`

- `SUCCESS`
- `FAIL`

### `SenderType`

Internal sender restriction enum:

- `ALL`
- `PLAYER`
- `CONSOLE`

## 🧰 Utility API

### `CommandUtil`

Public static helpers:

- `sendMessage(CommandSender sender, String... message)`
- `join(Object[] array, String separator)`
- `join(Iterable<?> iterable, String separator)`
- `getEnumByName(Class<? extends Enum<?>> clazz, String name)`
- `getEnumNames(Class<? extends Enum<?>> clazz)`
- `parseDuration(String text)`

## 🏗️ Typical Runtime Flow

1. create `CommandManager`
2. register custom argument resolvers and tab completers
3. register command classes
4. let `RegisteredCommand` and `CommandExecutor` handle dispatch

That is exactly how larger projects usually structure their command bootstrap.
