# command-lib

`command-lib` is a lightweight annotation-based command library for Paper plugins.
It registers commands directly in Bukkit's `CommandMap`, supports subcommands, argument parsing, and async tab completion without forcing you to keep large `plugin.yml` command sections.

## Highlights

- Annotation-driven commands
- Runtime command registration
- Subcommands and default handlers
- Built-in argument resolvers for common Bukkit/Paper types
- Async tab completion support
- Custom argument resolvers and custom tab completers
- Optional and joined arguments

## Requirements

- Java 21
- Tested with Paper `1.21.11`
- It may also work with other Paper, Spigot, or Bukkit versions, but that compatibility has not been tested or guaranteed
- If you need support for a specific server version, open an issue

## Installation

### Gradle Kotlin DSL

Add JitPack to your repositories:

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}
```

Add the dependency:

```kotlin
dependencies {
    implementation("com.github.thelipe7:command-lib:TAG")
}
```

### Gradle Groovy DSL

```groovy
repositories {
    mavenCentral()
    maven { url = 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.thelipe7:command-lib:TAG'
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.thelipe7</groupId>
        <artifactId>command-lib</artifactId>
        <version>TAG</version>
    </dependency>
</dependencies>
```

Replace `TAG` with a Git tag, release version, or commit hash from this repository.

## Quick Start

Create the manager in your plugin and register your command class:

```java
public final class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        CommandManager commandManager = new CommandManager(this, true);
        commandManager.registerCommand(new WarpCommand());
    }
}
```

Then create a command:

```java
import net.thelipe.command.CustomCommand;
import net.thelipe.command.annotation.Command;
import net.thelipe.command.annotation.Default;
import net.thelipe.command.annotation.Name;
import net.thelipe.command.annotation.Permission;
import net.thelipe.command.annotation.SubCommand;
import net.thelipe.command.annotation.TabComplete;
import net.thelipe.command.annotation.Unknown;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command({"warp", "warps"})
@Permission("example.warp")
public final class WarpCommand extends CustomCommand {

    @Default
    public void defaultCommand(CommandSender sender) {
        sender.sendMessage("Use /warp teleport <player>");
    }

    @SubCommand("teleport")
    @Permission("example.warp.teleport")
    @TabComplete("@players")
    public void teleport(CommandSender sender, @Name("player") Player target) {
        sender.sendMessage("Teleport target: " + target.getName());
    }

    @Unknown(true)
    public void unknown(CommandSender sender) {
        sender.sendMessage("Unknown subcommand.");
    }
}
```

## Argument Annotations

- `@Name("value")`: defines the argument name shown in usage messages
- `@Optional`: marks the last argument as optional
- `@Join`: joins the remaining input into one `String`

Example:

```java
@SubCommand("broadcast")
public void broadcast(CommandSender sender, @Join @Name("message") String message) {
    sender.getServer().broadcastMessage(message);
}
```

## Tab Completion

You can use built-in completions, literal values, or custom completers.

Literal values:

```java
@SubCommand("gamemode")
@TabComplete("@players survival|creative|adventure|spectator")
public void gamemode(CommandSender sender, Player target, GameMode mode) {
}
```

Register a custom completer by id:

```java
CommandManager manager = new CommandManager(this, true);
manager.registerTabCompleter("@warps", sender -> List.of("spawn", "shop", "pvp"));
```

Then reference it in a command method:

```java
@SubCommand("teleport")
@TabComplete("@warps")
public void teleport(Player player, String warpName) {
}
```

## Custom Argument Resolvers

You can register your own argument parser for any type:

```java
manager.registerArgumentResolver(MyType.class, (sender, input, argument) -> {
    MyType value = findMyType(input);
    if (value == null) {
        sender.sendMessage("Invalid value.");
        return new ArgumentResult<>(null, ResultStatus.FAIL);
    }

    return new ArgumentResult<>(value, ResultStatus.SUCCESS);
});
```

## Built-in Argument Support

Out of the box, the library includes resolvers for:

- `String`
- numeric primitives and wrappers
- `boolean`
- `Duration`
- `Player`
- enums
- `Enchantment`
- `ItemStack`
- `GameMode`

## Messages

Default error messages are customizable through `MessageProvider.setInstance(...)`, so you can replace permission, usage, invalid value, and sender-type messages with your own implementation.

## Issues and Pull Requests

Issues and pull requests are welcome.

If you find a bug, want support for a specific Paper, Spigot, or Bukkit version, or want to propose an improvement, open an issue with enough detail to reproduce or evaluate it.

If you want to contribute code, feel free to open a pull request.

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.
