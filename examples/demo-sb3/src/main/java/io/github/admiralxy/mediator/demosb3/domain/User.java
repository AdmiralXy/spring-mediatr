package io.github.admiralxy.mediator.demosb3.domain;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class User {

    @Nonnull
    private String name;
}
