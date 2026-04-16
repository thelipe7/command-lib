/*
 * This source file is part of command-lib.
 *
 * Copyright (c) 2026 thelipe7
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for license information.
 */

package net.thelipe.command.util;

/**
 * Status returned by argument resolution.
 */
public enum ResultStatus {

    /**
     * Argument resolution succeeded.
     */
    SUCCESS,
    /**
     * Argument resolution failed and command execution should stop.
     */
    FAIL

}
