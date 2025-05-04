package io.github.admiralxy.mediatr.api;

import java.util.function.Supplier;

/**
 * Interface for pipeline behavior. Can be used to implement cross-cutting concerns such as logging, validation, or transaction management.
 */
public interface PipelineBehavior {

    /**
     * Handles the message and calls the next behavior in the pipeline.
     *
     * @param message   message to handle
     * @param next      next behavior in the pipeline
     * @return result of the next behavior
     *
     * @param <R> result type
     */
    <R> R handle(Object message, Supplier<R> next);
}
