/*
 * This source file is part of command-lib.
 *
 * Copyright (c) 2026 thelipe7
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for license information.
 */

package net.thelipe.command;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import net.thelipe.command.annotation.Command;
import net.thelipe.command.annotation.Permission;
import net.thelipe.command.argument.Argument;
import net.thelipe.command.argument.ArgumentResolver;
import net.thelipe.command.argument.ArgumentResult;
import net.thelipe.command.util.CommandUtil;
import net.thelipe.command.util.ResultStatus;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Central registry for commands, argument resolvers, and tab completers.
 *
 * <p>Create a single instance during plugin startup, then use it to register command
 * classes and any custom parsing or completion behavior needed by your commands.</p>
 */
@Getter
public class CommandManager {

    @Getter private static CommandManager instance;

    private final JavaPlugin plugin;
    private final CommandMap bukkitCommandMap;
    private CommandTabCompleteManager tabCompleteManager;

    private final Map<String, RegisteredCommand> commands = new HashMap<>();
    private final Map<Class<?>, ArgumentResolver<?>> argumentResolvers = new HashMap<>();

    private final Map<String, Function<CommandSender, Collection<String>>> byIdTabCompleter = new HashMap<>();
    private final Map<Class<?>, BiFunction<CommandSender, Argument, Collection<String>>> byClassTabCompleter = new HashMap<>();

    /**
     * Creates a new command manager instance.
     *
     * @param plugin the owning plugin
     * @param autoRegisterTabCompleteManager whether the default async tab completion listener should be registered automatically
     * @throws IllegalStateException if a manager instance has already been created
     */
    public CommandManager(JavaPlugin plugin, boolean autoRegisterTabCompleteManager) {
        if (instance != null) {
            throw new IllegalStateException("CommandManager has already been initialized.");
        }

        instance = this;

        this.plugin = plugin;
        bukkitCommandMap = Bukkit.getServer().getCommandMap();
        if (autoRegisterTabCompleteManager) tabCompleteManager = new CommandTabCompleteManager(this);

        registerDefaults();
    }

    /**
     * Replaces the current tab completion manager.
     *
     * @param tabCompleteManager the new tab completion manager
     */
    public void setTabCompleteManager(CommandTabCompleteManager tabCompleteManager) {
        if (this.tabCompleteManager != null) {
            HandlerList.unregisterAll(this.tabCompleteManager);
        }

        this.tabCompleteManager = tabCompleteManager;
    }

    /**
     * Registers a command using metadata declared through annotations on the command class.
     *
     * @param customCommand the command instance to register
     * @return the registered Bukkit command wrapper
     */
    public RegisteredCommand registerCommand(CustomCommand customCommand) {
        Class<?> clazz = customCommand.getClass();

        Command commandAnnotation = clazz.getAnnotation(Command.class);
        if (commandAnnotation == null) throw new RuntimeException("Command annotation not found");

        Permission permission = clazz.getAnnotation(Permission.class);
        return registerCommand(customCommand, permission == null ? null : permission.value(), commandAnnotation.value());
    }

    /**
     * Registers a command using explicit names and permission values.
     *
     * @param customCommand the command instance to register
     * @param permission the base permission for the command, or {@code null} for no permission check
     * @param names the primary name followed by any aliases
     * @return the registered Bukkit command wrapper
     */
    public RegisteredCommand registerCommand(CustomCommand customCommand, String permission, String... names) {
        String commandName = names[0].toLowerCase();
        List<String> commandAliases = Stream.of(names).map(String::toLowerCase).filter(name -> !name.equals(commandName)).collect(Collectors.toList());

        RegisteredCommand registeredCommand = new RegisteredCommand(commandName, commandAliases, permission, customCommand, this);
        for (String name : registeredCommand.getNames()) commands.put(name, registeredCommand);

        org.bukkit.command.Command oldCommand = bukkitCommandMap.getCommand(registeredCommand.getFirstName());
        if (oldCommand != null) {
            bukkitCommandMap.getKnownCommands().remove(registeredCommand.getFirstName());
            oldCommand.unregister(bukkitCommandMap);
        }

        bukkitCommandMap.register(plugin.getName(), registeredCommand);
        return registeredCommand;
    }

    /**
     * Registers a custom argument resolver for a specific parameter type.
     *
     * @param clazz the parameter type supported by the resolver
     * @param argumentResolver the resolver implementation
     * @param <T> the argument type
     */
    public <T> void registerArgumentResolver(Class<T> clazz, ArgumentResolver<T> argumentResolver) {
        argumentResolvers.put(clazz, argumentResolver);
    }

    /**
     * Registers a tab completer that can be referenced from {@code @TabComplete} using an id such as {@code @players}.
     *
     * @param id the completer id
     * @param resolver the completion resolver
     */
    public void registerTabCompleter(String id, Function<CommandSender, Collection<String>> resolver) {
        byIdTabCompleter.put(id, resolver);
    }

    /**
     * Registers a tab completer for a specific parameter type.
     *
     * @param clazz the parameter type
     * @param resolver the completion resolver
     */
    public void registerTabCompleter(Class<?> clazz, BiFunction<CommandSender, Argument, Collection<String>> resolver) {
        byClassTabCompleter.put(clazz, resolver);
    }

    /**
     * Unregisters a previously registered command and all of its aliases.
     *
     * @param registeredCommand the command to unregister
     */
    public void unregisterCommand(RegisteredCommand registeredCommand) {
        for (String name : registeredCommand.getNames()) {
            commands.remove(name);

            org.bukkit.command.Command bukkitCommand = bukkitCommandMap.getCommand(name);
            if (bukkitCommand != null) {
                bukkitCommandMap.getKnownCommands().remove(name);
                bukkitCommand.unregister(bukkitCommandMap);
            }
        }
    }

    /**
     * Removes the argument resolver associated with a parameter type.
     *
     * @param clazz the parameter type
     */
    public void unregisterArgumentResolver(Class<?> clazz) {
        argumentResolvers.remove(clazz);
    }

    /**
     * Removes a tab completer registered by id.
     *
     * @param id the completer id
     */
    public void unregisterTabCompleter(String id) {
        byIdTabCompleter.remove(id);
    }

    /**
     * Removes a tab completer registered for a parameter type.
     *
     * @param clazz the parameter type
     */
    public void unregisterTabCompleter(Class<?> clazz) {
        byClassTabCompleter.remove(clazz);
    }

    @SuppressWarnings("unchecked")
    private void registerDefaults() {
        registerArgumentResolver(short.class, ((sender, input, argument) -> {
            try {
                short value = Short.parseShort(input);
                return new ArgumentResult<>(value, ResultStatus.SUCCESS);
            } catch (Exception e) {
                MessageProvider.getInstance().invalidValue(sender);
                return new ArgumentResult<>((short) 0, ResultStatus.FAIL);
            }
        }));
        registerArgumentResolver(Short.class, ((sender, input, argument) -> {
            try {
                short value = Short.parseShort(input);
                return new ArgumentResult<>(value, ResultStatus.SUCCESS);
            } catch (Exception e) {
                MessageProvider.getInstance().invalidValue(sender);
                return new ArgumentResult<>(null, ResultStatus.FAIL);
            }
        }));

        registerArgumentResolver(int.class, ((sender, input, argument) -> {
            try {
                int value = Integer.parseInt(input);
                return new ArgumentResult<>(value, ResultStatus.SUCCESS);
            } catch (Exception e) {
                MessageProvider.getInstance().invalidValue(sender);
                return new ArgumentResult<>(0, ResultStatus.FAIL);
            }
        }));
        registerArgumentResolver(Integer.class, ((sender, input, argument) -> {
            try {
                int value = Integer.parseInt(input);
                return new ArgumentResult<>(value, ResultStatus.SUCCESS);
            } catch (Exception e) {
                MessageProvider.getInstance().invalidValue(sender);
                return new ArgumentResult<>(null, ResultStatus.FAIL);
            }
        }));

        registerArgumentResolver(long.class, ((sender, input, argument) -> {
            try {
                long value = Long.parseLong(input);
                return new ArgumentResult<>(value, ResultStatus.SUCCESS);
            } catch (Exception e) {
                MessageProvider.getInstance().invalidValue(sender);
                return new ArgumentResult<>((long) 0, ResultStatus.FAIL);
            }
        }));
        registerArgumentResolver(Long.class, ((sender, input, argument) -> {
            try {
                long value = Long.parseLong(input);
                return new ArgumentResult<>(value, ResultStatus.SUCCESS);
            } catch (Exception e) {
                MessageProvider.getInstance().invalidValue(sender);
                return new ArgumentResult<>(null, ResultStatus.FAIL);
            }
        }));

        registerArgumentResolver(float.class, ((sender, input, argument) -> {
            try {
                float value = Float.parseFloat(input);
                return new ArgumentResult<>(value, ResultStatus.SUCCESS);
            } catch (Exception e) {
                MessageProvider.getInstance().invalidValue(sender);
                return new ArgumentResult<>((float) 0.0, ResultStatus.FAIL);
            }
        }));
        registerArgumentResolver(Float.class, ((sender, input, argument) -> {
            try {
                float value = Float.parseFloat(input);
                return new ArgumentResult<>(value, ResultStatus.SUCCESS);
            } catch (Exception e) {
                MessageProvider.getInstance().invalidValue(sender);
                return new ArgumentResult<>(null, ResultStatus.FAIL);
            }
        }));

        registerArgumentResolver(double.class, ((sender, input, argument) -> {
            try {
                double value = Double.parseDouble(input);
                return new ArgumentResult<>(value, ResultStatus.SUCCESS);
            } catch (Exception e) {
                MessageProvider.getInstance().invalidValue(sender);
                return new ArgumentResult<>(0.0, ResultStatus.FAIL);
            }
        }));
        registerArgumentResolver(Double.class, ((sender, input, argument) -> {
            try {
                double value = Double.parseDouble(input);
                return new ArgumentResult<>(value, ResultStatus.SUCCESS);
            } catch (Exception e) {
                MessageProvider.getInstance().invalidValue(sender);
                return new ArgumentResult<>(null, ResultStatus.FAIL);
            }
        }));

        registerArgumentResolver(boolean.class,((sender, input, argument) ->
                new ArgumentResult<>(Boolean.valueOf(input), ResultStatus.SUCCESS)));
        registerArgumentResolver(Boolean.class,((sender, input, argument) ->
                new ArgumentResult<>(Boolean.valueOf(input), ResultStatus.SUCCESS)));
        registerTabCompleter(boolean.class, (sender, argument) ->
                ImmutableList.of("true", "false"));
        registerTabCompleter(Boolean.class, (sender, argument) ->
                ImmutableList.of("true", "false"));

        registerArgumentResolver(Duration.class,((sender, input, argument) -> {
            Duration duration = CommandUtil.parseDuration(input);
            if (duration == null) {
                MessageProvider.getInstance().invalidDuration(sender);
                return new ArgumentResult<>(null, ResultStatus.FAIL);
            }

            return new ArgumentResult<>(duration, ResultStatus.SUCCESS);
        }));
        registerTabCompleter(Duration.class, (sender, argument) ->
                ImmutableList.of("10s", "1m30s", "30m", "1h45m", "3h", "1d", "1d3h15m"));

        registerArgumentResolver(Player.class, ((sender, input, argument) -> {
            Player player = Bukkit.getPlayer(input);
            if (player == null) {
                MessageProvider.getInstance().playerNotFound(sender);
                return new ArgumentResult<>(null, ResultStatus.FAIL);
            }

            return new ArgumentResult<>(player, ResultStatus.SUCCESS);
        }));
        registerTabCompleter(Player.class, ((sender, argument) ->
                Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList())));
        registerTabCompleter("@players", sender ->
                Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));

        registerArgumentResolver(Enum.class, ((sender, input, argument) -> {
            Enum<?> enumObject = CommandUtil.getEnumByName((Class<? extends Enum<?>>) argument.getClazz(), input.toUpperCase());
            if (enumObject == null) return new ArgumentResult<>(null, ResultStatus.FAIL);

            return new ArgumentResult<>(enumObject, ResultStatus.SUCCESS);
        }));
        registerTabCompleter(Enum.class, ((sender, argument) ->
                CommandUtil.getEnumNames((Class<? extends Enum<?>>) argument.getClazz())));

        registerArgumentResolver(Enchantment.class, (sender, input, argument) -> {
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(input));
            if (enchantment == null) {
                CommandUtil.sendMessage(sender, "<red>Encantamento não encontrado.");
                return new ArgumentResult<>(null, ResultStatus.FAIL);
            }

            return new ArgumentResult<>(enchantment, ResultStatus.SUCCESS);
        });

        registerTabCompleter(Enchantment.class, (sender, argument) ->
                Arrays.stream(Enchantment.values()).map(enchantment -> enchantment.getKey().getKey()).toList());

        registerArgumentResolver(ItemStack.class, (sender, input, argument) -> {
            String[] split = input.split(":");
            if (split.length == 2) {
                try {
                    Material material = (Material) CommandUtil.getEnumByName(Material.class, split[0]);
                    if (material == null) {
                        CommandUtil.sendMessage(sender, "<red>Item não encontrado.");
                        return new ArgumentResult<>(null, ResultStatus.FAIL);
                    }

                    ItemStack itemStack = new ItemStack(material, 0, Short.parseShort(split[1]));
                    return new ArgumentResult<>(itemStack, ResultStatus.SUCCESS);
                } catch (Exception e) {
                    CommandUtil.sendMessage(sender, "<red>Item não encontrado.");
                    return new ArgumentResult<>(null, ResultStatus.FAIL);
                }
            } else {
                Material material = (Material) CommandUtil.getEnumByName(Material.class, split[0]);
                if (material == null) {
                    CommandUtil.sendMessage(sender, "<red>Item não encontrado.");
                    return new ArgumentResult<>(null, ResultStatus.FAIL);
                }

                ItemStack itemStack = new ItemStack(material);
                return new ArgumentResult<>(itemStack, ResultStatus.SUCCESS);
            }
        });
        registerTabCompleter(ItemStack.class, (sender, argument) ->
                CommandUtil.getEnumNames(Material.class));

        registerArgumentResolver(GameMode.class, (sender, input, argument) -> {
            GameMode gameMode = (GameMode) CommandUtil.getEnumByName(GameMode.class, input);
            if (gameMode == null) {
                try {
                    int id = Integer.parseInt(input);
                    if (id == 0) {
                        gameMode = GameMode.SURVIVAL;
                    } else if (id == 1) {
                        gameMode = GameMode.CREATIVE;
                    } else if (id == 2) {
                        gameMode = GameMode.ADVENTURE;
                    } else if (id == 3) {
                        gameMode = GameMode.SPECTATOR;
                    }
                } catch (Exception e) {
                    CommandUtil.sendMessage(sender, "<red>Modo de jogo não encontrado.");
                    return new ArgumentResult<>(null, ResultStatus.FAIL);
                }
            }

            if (gameMode == null) {
                CommandUtil.sendMessage(sender, "<red>Modo de jogo não encontrado.");
                return new ArgumentResult<>(null, ResultStatus.FAIL);
            }

            return new ArgumentResult<>(gameMode, ResultStatus.SUCCESS);
        });
    }

}
