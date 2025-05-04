package io.github.admiralxy.mediator.demosb3.pipeline;

import io.github.admiralxy.mediatr.api.PipelineBehavior;
import io.github.admiralxy.mediatr.autoconfigure.Pipeline;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
@Pipeline
public class LoggingPipelineBehavior implements PipelineBehavior {

    @Override
    public <R> R handle(Object message, Supplier<R> next) {
        log.info("LoggingPipelineBehavior: {}", message);
        return next.get();
    }
}
