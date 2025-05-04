package io.github.admiralxy.mediatr.core;

import io.github.admiralxy.mediatr.api.Mediator;
import io.github.admiralxy.mediatr.api.Notification;
import io.github.admiralxy.mediatr.api.PipelineBehavior;
import io.github.admiralxy.mediatr.api.Request;
import io.github.admiralxy.mediatr.api.handler.NotificationHandler;
import io.github.admiralxy.mediatr.api.handler.RequestHandler;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
public class MediatorImpl implements Mediator {

    private final Map<Class<?>, Object> requestHandlers;
    private final Map<Class<?>, List<Object>> notificationHandlers;

    private final List<PipelineBehavior> pipeline;
    private final Executor executor;

    public <R> R send(Request<R> request) {
        return invokeSync(request, findHandler(request.getClass()));
    }

    public <R> CompletableFuture<R> sendAsync(Request<R> request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return send(request);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    public void publish(Notification notification) {
        for (Object h : findAll(notification.getClass())) {
            invokeSync(notification, h);
        }
    }

    public CompletableFuture<Void> publishAsync(Notification notification) {
        List<CompletableFuture<Void>> futures = findAll(notification.getClass()).stream()
                .map(h -> CompletableFuture.runAsync(() -> {
                    try {
                        invokeSync(notification, h);
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                }, executor))
                .toList();
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private Object findHandler(Class<?> key) {
        return Optional.ofNullable(requestHandlers.get(key))
                .orElseThrow(() -> new IllegalStateException("No handler for " + key));
    }

    private Collection<Object> findAll(Class<?> key) {
        if (Notification.class.isAssignableFrom(key)) {
            return notificationHandlers.getOrDefault(key, List.of());
        }
        return requestHandlers.entrySet().stream()
                .filter(e -> e.getKey().isAssignableFrom(key))
                .map(Map.Entry::getValue)
                .toList();
    }

    private <R> R invokeSync(Object msg, Object handler) {
        Supplier<R> terminal = () -> {
            try {
                if (handler instanceof RequestHandler<?, ?> rh) {
                    return (R) ((RequestHandler) rh).handle((Request) msg);
                }
                if (handler instanceof NotificationHandler<?> nh) {
                    ((NotificationHandler) nh).handle((Notification) msg);
                    return null;
                }
                throw new IllegalStateException("Unsupported handler type " + handler);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        Supplier<R> chain = pipeline.stream()
                .reduce(
                        terminal,
                        (next, behavior) -> () -> {
                            try {
                                return behavior.handle(msg, next);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        },
                        (a, b) -> a
                );
        return chain.get();
    }
}
