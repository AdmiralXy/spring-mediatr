package io.github.admiralxy.mediatr.api.handler;

import io.github.admiralxy.mediatr.api.Request;

/**
 * Tag interface for request handlers.
 *
 * @param <Q> request type
 * @param <R> response type
 */
@FunctionalInterface
public interface RequestHandler<Q extends Request<R>, R> {

    /**
     * Handles request.
     *
     * @param request request to handle
     * @return response
     */
    R handle(Q request);
}
