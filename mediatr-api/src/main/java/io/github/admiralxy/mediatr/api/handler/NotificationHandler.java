package io.github.admiralxy.mediatr.api.handler;

import io.github.admiralxy.mediatr.api.Notification;

/**
 * Tag interface for notification handlers.
 *
 * @param <N> notification type
 */
@FunctionalInterface
public interface NotificationHandler<N extends Notification> {

    /**
     * Handles notification.
     *
     * @param notification notification to handle
     */
    void handle(N notification);
}
