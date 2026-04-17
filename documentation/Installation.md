# ⚙️ Installation

## ✅ Requirements

- Java 21
- Paper `1.21.11`

The project is built against Paper `1.21.11`. Other versions may work, but compatibility outside that target is not guaranteed by the current repository state.

## 📦 Gradle Kotlin DSL

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.thelipe7:command-lib:TAG")
}
```

## 📦 Gradle Groovy DSL

```groovy
repositories {
    mavenCentral()
    maven { url = 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.thelipe7:command-lib:TAG'
}
```

## 📦 Maven

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

## 🧱 Creating the Manager

Create a single `CommandManager` during plugin startup:

```java
public final class ExamplePlugin extends JavaPlugin {

    private CommandManager commandManager;

    @Override
    public void onEnable() {
        commandManager = new CommandManager(this, true);
    }

}
```

## ⌨️ Automatic Tab Completion Registration

`new CommandManager(plugin, true)` automatically registers `CommandTabCompleteManager`, the built-in async tab completion listener.

Use `false` when:

- you want to install your own tab completion manager
- you want to disable the default listener
- you want manual control over listener registration

Example:

```java
CommandManager manager = new CommandManager(this, false);
manager.setTabCompleteManager(new CommandTabCompleteManager(manager));
```

## 📝 What the Library Does not Need

For commands managed by `command-lib`, you do not need to keep large command declarations in `plugin.yml`, because the framework registers commands at runtime through Bukkit's `CommandMap`.
