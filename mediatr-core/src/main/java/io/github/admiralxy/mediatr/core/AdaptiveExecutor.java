package io.github.admiralxy.mediatr.core;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public final class AdaptiveExecutor {

    private static final AtomicInteger poolCounter = new AtomicInteger(1);

    @SuppressWarnings("resource")
    public static ExecutorService create(boolean isVirtualThreads, int corePool, int maxPool, Duration keepAlive, int queueCapacity) {
        ExecutorService executorService;
        if (isVirtualThreads && VirtualHolder.AVAILABLE) {
            executorService = VirtualHolder.EXEC;
        } else {
            ThreadPoolExecutor exec = new ThreadPoolExecutor(
                    corePool,
                    maxPool,
                    keepAlive.toSeconds(),
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(queueCapacity),
                    r -> new Thread(r, "mediatr-pool-" + poolCounter.getAndIncrement()),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
            exec.allowCoreThreadTimeOut(true);
            executorService = exec;
        }

        log.info("Using {} executor: core={}, max={}, keepAlive={}s, queue={}",
                isVirtualThreads ? "virtual" : "platform",
                isVirtualThreads ? "-" : corePool,
                isVirtualThreads ? "-" : maxPool,
                isVirtualThreads ? "-" : keepAlive.getSeconds(),
                isVirtualThreads ? "-" : queueCapacity
        );

        return executorService;
    }

    protected static final class VirtualHolder {
        static final boolean AVAILABLE;
        static final ExecutorService EXEC;

        static {
            ExecutorService tmp = null;
            boolean ok;
            try {
                Method m = Executors.class
                        .getMethod("newVirtualThreadPerTaskExecutor");
                tmp = (ExecutorService) m.invoke(null);
                ok  = true;
            } catch (Throwable t) {
                ok = false;
            }
            AVAILABLE = ok;
            EXEC = tmp;
        }
    }
}
