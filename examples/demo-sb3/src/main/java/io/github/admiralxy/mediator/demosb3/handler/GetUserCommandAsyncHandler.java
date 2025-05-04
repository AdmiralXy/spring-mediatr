package io.github.admiralxy.mediator.demosb3.handler;

import io.github.admiralxy.mediator.demosb3.domain.User;
import io.github.admiralxy.mediator.demosb3.request.GetUserQuery;
import io.github.admiralxy.mediatr.api.handler.RequestHandler;
import io.github.admiralxy.mediatr.autoconfigure.Handler;

@Handler
public class GetUserCommandAsyncHandler implements RequestHandler<GetUserQuery, User> {

    @Override
    public User handle(GetUserQuery request) {
        return new User(request.name());
    }
}
