package net.thelipe.command.argument;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.thelipe.command.util.ResultStatus;

@Getter
@AllArgsConstructor
public class ArgumentResult<T> {

    private final T result;
    private final ResultStatus status;

}
