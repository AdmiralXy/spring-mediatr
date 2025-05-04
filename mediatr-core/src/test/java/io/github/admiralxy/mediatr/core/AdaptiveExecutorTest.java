package io.github.admiralxy.mediatr.core;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class AdaptiveExecutorTest {

    /* ---------- tests ---------- */

    @Test
    @DisplayName("create(false, n) ThreadPoolExecutor with given parallelism")
    void platformPool_hasExpectedParallelism() {
        // GIVEN
        int parallelism = 5;

        // WHEN
        ExecutorService exec = AdaptiveExecutor.create(false, parallelism, parallelism, Duration.ofSeconds(60), 100);

        // THEN
        assertThat(exec).isInstanceOf(ThreadPoolExecutor.class);
        assertThat(((ThreadPoolExecutor) exec).getCorePoolSize()).isEqualTo(parallelism);
        assertThat(((ThreadPoolExecutor) exec).getMaximumPoolSize()).isEqualTo(parallelism);
        assertThat(((ThreadPoolExecutor) exec).getKeepAliveTime(TimeUnit.SECONDS)).isEqualTo(60);
        assertThat(((ThreadPoolExecutor) exec).getQueue().remainingCapacity()).isEqualTo(100);

        exec.shutdownNow();
    }

    @Test
    @DisplayName("create(true, n) falls back to ThreadPoolExecutor when Loom is unavailable")
    void virtualThreadsUnavailable_fallbackToPlatform() {
        // Skip on JDK 21+ where Loom is present
        Assumptions.assumeFalse(AdaptiveExecutor.VirtualHolder.AVAILABLE);

        // WHEN
        ExecutorService exec = AdaptiveExecutor.create(true, 3, 3, Duration.ofSeconds(60), 100);

        // THEN
        assertThat(exec).isInstanceOf(ThreadPoolExecutor.class);
        exec.shutdownNow();
    }

    @Test
    @DisplayName("create(true, n) returns shared virtual-thread executor when Loom is available")
    void virtualThreadsAvailable_useVirtualExecutor() throws Exception {
        // Run only on JDK 21+ where virtual threads exist
        Assumptions.assumeTrue(AdaptiveExecutor.VirtualHolder.AVAILABLE);

        // WHEN
        ExecutorService exec = AdaptiveExecutor.create(true, 8, 8, Duration.ofSeconds(60), 100);

        // THEN
        assertThat(exec).isSameAs(AdaptiveExecutor.VirtualHolder.EXEC);

        CompletableFuture<Integer> f = new CompletableFuture<>();
        exec.execute(() -> f.complete(42));
        assertThat(f.get()).isEqualTo(42);
    }
}
