package io.github.admiralxy.mediator.demosb3.handler;

import io.github.admiralxy.mediator.demosb3.request.SendEmailToUserNotification;
import io.github.admiralxy.mediatr.api.handler.NotificationHandler;
import io.github.admiralxy.mediatr.autoconfigure.Handler;

@Handler
public class SendEmailToUserFirstHandler implements NotificationHandler<SendEmailToUserNotification> {

    @Override
    public void handle(SendEmailToUserNotification notification) {
        System.out.println("Send email to user first handler: " + notification.email());
    }
}
