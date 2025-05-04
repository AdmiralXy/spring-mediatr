package io.github.admiralxy.mediatr.api;

import java.util.concurrent.CompletableFuture;

/**
 * Mediator interface for sending requests and publishing notifications.
 */
public interface Mediator {

    /**
     * Sends a request and returns a response.
     *
     * @param request request to send
     * @return response
     * @param <R> response type
     */
    <R> R send(Request<R> request);

    /**
     * Sends a request asynchronously and returns a {@link CompletableFuture} with the response.
     *
     * @param request request to send
     * @return a {@link CompletableFuture} with the response
     * @param <R> response type
     */
    <R> CompletableFuture<R> sendAsync(Request<R> request);

    /**
     * Publishes a notification.
     *
     * @param notification notification to publish
     */
    void publish(Notification notification);

    /**
     * Publishes a notification asynchronously and returns a {@link CompletableFuture}.
     *
     * @param notification notification to publish
     * @return a {@link CompletableFuture}
     */
    CompletableFuture<Void> publishAsync(Notification notification);
}
