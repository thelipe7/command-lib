<div align="center">
  <img src="icon.svg" width="120" alt="command-lib icon">

  <h1>command-lib</h1>

  <p><em>Annotation-driven commands for Paper plugins.</em></p>

  <p>Runtime registration, typed arguments, subcommands, permissions, and async tab completion without large <code>plugin.yml</code> command sections.</p>

  <p>
    <a href="https://github.com/thelipe7/command-lib/commits/main">
      <img alt="Last commit" src="https://img.shields.io/github/last-commit/thelipe7/command-lib?style=flat-square&logo=github">
    </a>
    <a href="https://github.com/thelipe7/command-lib/issues">
      <img alt="GitHub issues" src="https://img.shields.io/github/issues/thelipe7/command-lib?style=flat-square&logo=github">
    </a>
    <a href="https://github.com/thelipe7/command-lib/stargazers">
      <img alt="GitHub stars" src="https://img.shields.io/github/stars/thelipe7/command-lib?style=flat-square&logo=github">
    </a>
    <a href="https://github.com/thelipe7/command-lib/blob/main/LICENSE">
      <img alt="License" src="https://img.shields.io/github/license/thelipe7/command-lib?style=flat-square">
    </a>
  </p>


[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges/assets/cozy/built-with/java21_vector.svg)](https://adoptium.net/pt-BR/temurin/releases?version=21&os=any&arch=any)
[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges/assets/cozy/built-with/gradle_vector.svg)](https://gradle.org/)

[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges/assets/cozy/available/github_vector.svg)](https://github.com/thelipe7/command-lib)
[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/project/command-lib)
[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges/assets/cozy/available/hangar_vector.svg)](https://hangar.papermc.io/TheLipe/command-lib)

  <p>
    <a href="https://github.com/thelipe7/command-lib/wiki">Documentation</a>
    |
    <a href="https://github.com/thelipe7/command-lib/issues">Suport</a>
    |
    <a href="https://github.com/thelipe7/command-lib/blob/main/CONTRIBUTING.md">Contributing</a>
    |
    <a href="https://github.com/thelipe7/command-lib/blob/main/LICENSE">License</a>
  </p>
</div>

---

## Overview

`command-lib` is a lightweight command framework for Paper plugins built around annotations and runtime registration.

It keeps command code direct while covering the repetitive parts that usually spread across plugin projects:

- command registration without large `plugin.yml` command sections
- typed argument parsing for common Bukkit and Paper types
- subcommands, default handlers, and unknown handlers
- permission handling at class and method level
- async tab completion support on Paper
- custom argument resolvers and custom tab completers

## Requirements

[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges/assets/cozy/supported/paper_vector.svg)](https://papermc.io/software/paper/)
[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges/assets/cozy/supported/purpur_vector.svg)](https://purpurmc.org/)

Other Bukkit-compatible servers may work, but the current repository targets and documents Paper `1.20+`.

## Installation

> [!NOTE]\
> Replace `TAG` with a Git tag, release version, or commit hash from this repository.

<details open>
<summary>Gradle (Kotlin DSL)</summary>

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.thelipe7:command-lib:TAG")
}
```
</details>

<details>
<summary>Gradle (Groovy DSL)</summary>

```groovy
repositories {
    mavenCentral()
    maven { url = 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.thelipe7:command-lib:TAG'
}
```
</details>

<details>
<summary>Maven</summary>

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
</details>

## Quick Start

Create a manager in your plugin:

```java
public final class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        CommandManager manager = new CommandManager(this, true);
        manager.registerCommand(new WarpCommand());
    }

}
```

Then define a command class:

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

## Documentation

[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges/assets/cozy/documentation/ghpages_vector.svg)](https://github.com/thelipe7/command-lib/wiki)

- [Installation](https://github.com/thelipe7/command-lib/wiki/Installation)
- [Getting Started](https://github.com/thelipe7/command-lib/wiki/Getting-Started)
- [Defining Commands](https://github.com/thelipe7/command-lib/wiki/Defining-Commands)
- [Arguments and Resolvers](https://github.com/thelipe7/command-lib/wiki/Arguments-and-Resolvers)
- [Tab Completion](https://github.com/thelipe7/command-lib/wiki/Tab-Completion)
- [Permissions and Messages](https://github.com/thelipe7/command-lib/wiki/Permissions-and-Messages)
- [API Reference](https://github.com/thelipe7/command-lib/wiki/API-Reference)
- [Troubleshooting](https://github.com/thelipe7/command-lib/wiki/Troubleshooting)

## Support

Use the issue templates for:

- bugs and regressions
- feature requests
- usage questions

[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges/assets/cozy/documentation/issues_vector.svg)](https://github.com/thelipe7/command-lib/issues)
[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges/assets/cozy/social/discord-singular_vector.svg)](https://discord.com/users/842438483686719488)

## Contributing

See [CONTRIBUTING](https://github.com/thelipe7/command-lib?tab=contributing-ov-file) for contribution guidelines and review expectations.

[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges/assets/cozy/documentation/pull-requests_vector.svg)](https://github.com/thelipe7/command-lib/pulls)

## Sponsors

Thank you so much for considering helping me, I truly appreciate it!

[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges/assets/cozy/donate/ghsponsors-singular_vector.svg)](https://github.com/sponsors/thelipe7)
[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges/assets/cozy/donate/buymeacoffee-singular_vector.svg)](https://buymeacoffee.com/thelipe7)
[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges/assets/cozy/donate/paypal-singular_vector.svg)](https://www.paypal.com/donate/?hosted_button_id=3PB97S5NAJMYA)

## License

This project is licensed under the Apache License 2.0.

See [LICENSE](https://github.com/thelipe7/command-lib/blob/main/LICENSE) for details.
