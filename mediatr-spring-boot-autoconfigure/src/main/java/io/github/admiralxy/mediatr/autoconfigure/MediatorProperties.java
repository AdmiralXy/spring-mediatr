package io.github.admiralxy.mediatr.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Mediator properties.
 */
@Getter
@Setter
@ConfigurationProperties("mediator")
public class MediatorProperties {

    /**
     * Core parallel tasks, default: availableProcessors (ignored for virtual threads)
     */
    private int corePool = Runtime.getRuntime().availableProcessors();

    /**
     * Max parallel tasks, default: 2 * availableProcessors (ignored for virtual threads)
     */
    private int maxPool = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * Duration of keep-alive time for idle threads, default: 60 seconds (ignored for virtual threads)
     */
    private Duration keepAlive = Duration.ofSeconds(60);

    /**
     * Queue capacity, default: 10000 (ignored for virtual threads)
     */
    private int queueCapacity = 10000;

    /**
     * Enable virtual threads when running on JDK 21+
     */
    private boolean virtualThreads = true;
}
