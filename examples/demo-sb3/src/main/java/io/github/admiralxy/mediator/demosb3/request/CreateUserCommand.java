package io.github.admiralxy.mediator.demosb3.request;

import io.github.admiralxy.mediator.demosb3.domain.User;
import io.github.admiralxy.mediatr.api.Request;
import jakarta.annotation.Nonnull;

public record CreateUserCommand(@Nonnull String name) implements Request<User> { }
