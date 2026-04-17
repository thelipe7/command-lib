# 📚 command-lib Documentation

Welcome to the official `command-lib` documentation.

`command-lib` is a lightweight annotation-driven command framework for Paper plugins. It registers commands directly in Bukkit's `CommandMap`, supports subcommands, parses typed arguments, and provides async tab completion without forcing large `plugin.yml` command sections.

## ✨ Start Here

- [⚙️ Installation](Installation)
- [🚀 Getting Started](Getting-Started)
- [🧩 Defining Commands](Defining-Commands)
- [🔎 Arguments and Resolvers](Arguments-and-Resolvers)
- [⌨️ Tab Completion](Tab-Completion)
- [🔐 Permissions and Messages](Permissions-and-Messages)
- [🧪 API Reference](API-Reference)
- [🛠️ Troubleshooting](Troubleshooting)
- [🤝 Contributing and Support](Contributing-and-Support)

## 🌟 Highlights

- Annotation-driven command classes
- Runtime registration through Bukkit's `CommandMap`
- Default handlers, subcommands, and unknown-command fallbacks
- Built-in resolvers for common Paper and Bukkit types
- Custom argument resolvers and custom tab completers
- Async tab completion support on Paper
- Optional and joined arguments

## ✅ Requirements

- Java 21
- Paper `1.20+`

Other Bukkit-compatible servers may work, but the current repository targets and documents Paper `1.20+`.

## 🚀 Quick Example

```java
public final class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        CommandManager manager = new CommandManager(this, true);
        manager.registerCommand(new WarpCommand());
    }

}
```

```java
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

## 🏗️ Architecture Overview

At runtime the library works in three layers:

1. `CommandManager` registers commands, argument resolvers, and tab completers.
2. `RegisteredCommand` scans a `CustomCommand` class and builds runtime executors from annotations.
3. `CommandExecutor` validates sender type, resolves arguments, and invokes the target method.

## 📖 Reading Strategy

If you are new to the project, read the pages in this order:

1. [Installation](Installation)
2. [Getting Started](Getting-Started)
3. [Defining Commands](Defining-Commands)
4. [Arguments and Resolvers](Arguments-and-Resolvers)
5. [Tab Completion](Tab-Completion)
6. [Permissions and Messages](Permissions-and-Messages)
