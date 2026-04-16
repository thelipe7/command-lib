/*
 * This source file is part of command-lib.
 *
 * Copyright (c) 2026 thelipe7
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for license information.
 */

package net.thelipe.command.argument;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Describes a parsed command method parameter.
 */
@Getter
@AllArgsConstructor
public class Argument {

    /**
     * Argument display name used in generated usage messages.
     */
    private final String name;
    /**
     * Java type expected by the command handler.
     */
    private final Class<?> clazz;
    /**
     * Zero-based argument index in the raw command input.
     */
    private final int position;
    /**
     * Whether the argument may be omitted.
     */
    private final boolean optional;
    /**
     * Whether the argument consumes the remaining input.
     */
    private final boolean join;

}
