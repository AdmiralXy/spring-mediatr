package io.github.admiralxy.mediator.demosb3.handler;

import io.github.admiralxy.mediator.demosb3.domain.User;
import io.github.admiralxy.mediator.demosb3.request.CreateUserCommand;
import io.github.admiralxy.mediatr.api.handler.RequestHandler;
import io.github.admiralxy.mediatr.autoconfigure.Handler;

@Handler
public class CreateUserCommandHandler implements RequestHandler<CreateUserCommand, User> {

    @Override
    public User handle(CreateUserCommand request) {
        return new User(request.name());
    }
}
