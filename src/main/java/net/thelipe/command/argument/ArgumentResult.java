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
import net.thelipe.command.util.ResultStatus;

/**
 * Result returned by an {@link ArgumentResolver}.
 *
 * @param <T> the resolved argument type
 */
@Getter
@AllArgsConstructor
public class ArgumentResult<T> {

    /**
     * The resolved value, or {@code null} when resolution fails for nullable types.
     */
    private final T result;
    /**
     * Indicates whether the resolution succeeded.
     */
    private final ResultStatus status;

}
