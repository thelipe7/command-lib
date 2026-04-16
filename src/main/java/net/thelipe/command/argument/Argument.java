package net.thelipe.command.argument;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Argument {

    private final String name;
    private final Class<?> clazz;
    private final int position;
    private final boolean optional;
    private final boolean join;

}
