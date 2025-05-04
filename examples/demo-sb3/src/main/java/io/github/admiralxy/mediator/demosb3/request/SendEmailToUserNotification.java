package io.github.admiralxy.mediator.demosb3.request;

import io.github.admiralxy.mediatr.api.Notification;

public record SendEmailToUserNotification(String email) implements Notification {
}
