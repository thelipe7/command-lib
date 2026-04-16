/*
 * This source file is part of command-lib.
 *
 * Copyright (c) 2026 thelipe7
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for license information.
 */

package net.thelipe.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as the fallback handler for unmatched subcommands.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Unknown {

    /**
     * @return {@code true} to also expose this handler as a {@code help} subcommand
     */
    boolean value();

    /**
     * @return the permission required to access the generated {@code help} subcommand
     */
    String helpPermission() default "";

}
