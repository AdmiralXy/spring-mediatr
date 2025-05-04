package io.github.admiralxy.mediatr.core;

import io.github.admiralxy.mediatr.api.Mediator;
import io.github.admiralxy.mediatr.api.Notification;
import io.github.admiralxy.mediatr.api.PipelineBehavior;
import io.github.admiralxy.mediatr.api.Request;
import io.github.admiralxy.mediatr.api.handler.NotificationHandler;
import io.github.admiralxy.mediatr.api.handler.RequestHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class MediatorImplTest {

    /* ---------- dummy messages ---------- */

    record GetUser(UUID id) implements Request<String> {}

    record OrderPlaced(UUID id) implements Notification {}

    /* ---------- collaborators ---------- */

    @SuppressWarnings("unchecked")
    private final RequestHandler<GetUser, String> getUserHandler = mock(RequestHandler.class);

    @SuppressWarnings("unchecked")
    private final NotificationHandler<OrderPlaced> orderPlacedHandler = mock(NotificationHandler.class);

    private final PipelineBehavior pipelineBehavior = mock(PipelineBehavior.class);

    private Mediator mediator;

    /* ---------- test fixture ---------- */

    @BeforeEach
    void setUp() {
        when(getUserHandler.handle(any())).thenReturn("John Doe");
        when(pipelineBehavior.handle(any(), any())).thenAnswer(inv -> {
            Supplier<?> next = inv.getArgument(1);
            return next.get();
        });

        Map<Class<?>, Object> requestHandlers = Map.of(
                GetUser.class, getUserHandler
        );
        Map<Class<?>, List<Object>> notificationHandlers = Map.of(
                OrderPlaced.class, List.of(orderPlacedHandler)
        );

        ExecutorService executor = Executors.newSingleThreadExecutor();
        mediator = new MediatorImpl(requestHandlers, notificationHandlers, List.of(pipelineBehavior), executor);
    }

    /* ---------- tests ---------- */

    @DisplayName("send()/sendAsync(): returns handler result and delegates exactly once")
    @ParameterizedTest(name = "async = {0}")
    @ValueSource(booleans = {false, true})
    void send_syncOrAsync_thenReturnResult(boolean async) throws Exception {
        // GIVEN
        GetUser query = new GetUser(UUID.randomUUID());

        // WHEN
        String result = async
                ? mediator.sendAsync(query).get(1, TimeUnit.SECONDS)
                : mediator.send(query);

        // THEN
        assertThat(result).isEqualTo("John Doe");
        verify(getUserHandler, times(1)).handle(query);
        verifyNoInteractions(orderPlacedHandler);

        verify(pipelineBehavior, times(1)).handle(eq(query), any());
    }

    @Test
    @DisplayName("publish()/publishAsync(): invokes all notification handlers")
    void publish_syncOrAsync_thenAllHandlersInvoked() throws Exception {
        // GIVEN
        OrderPlaced evt = new OrderPlaced(UUID.randomUUID());

        // WHEN
        mediator.publish(evt);
        mediator.publishAsync(evt).get(1, TimeUnit.SECONDS);

        // THEN
        verify(orderPlacedHandler, times(2)).handle(evt);
        verifyNoInteractions(getUserHandler);

        verify(pipelineBehavior, times(2)).handle(eq(evt), any());
    }

    /* ---------- error-handling tests ---------- */

    @DisplayName("Handler throws and mediator propagates (sync / async)")
    @ParameterizedTest(name = "async = {0}")
    @ValueSource(booleans = {false, true})
    void handlerThrows_syncOrAsync_thenExceptionPropagated(boolean async) {
        // GIVEN
        RuntimeException boom = new IllegalStateException("boom");
        when(getUserHandler.handle(any())).thenThrow(boom);
        doThrow(boom).when(orderPlacedHandler).handle(any());

        GetUser      query = new GetUser(UUID.randomUUID());
        OrderPlaced  evt   = new OrderPlaced(UUID.randomUUID());

        // WHEN | THEN — Request
        if (async) {
            assertThatThrownBy(() -> mediator.sendAsync(query).get(1, TimeUnit.SECONDS))
                    .hasRootCause(boom);
        } else {
            assertThatThrownBy(() -> mediator.send(query))
                    .hasRootCause(boom);
        }

        // WHEN | THEN — Notification
        if (async) {
            assertThatThrownBy(() -> mediator.publishAsync(evt).get(1, TimeUnit.SECONDS))
                    .hasRootCause(boom);
        } else {
            assertThatThrownBy(() -> mediator.publish(evt))
                    .hasRootCause(boom);
        }
    }

    @DisplayName("Pipeline throws and mediator propagates & stops chain (sync / async)")
    @ParameterizedTest(name = "async = {0}")
    @ValueSource(booleans = {false, true})
    void pipelineThrows_syncOrAsync_thenHandlersNotInvoked(boolean async) {
        // GIVEN
        RuntimeException oops = new IllegalArgumentException("pipeline oops");
        reset(pipelineBehavior, getUserHandler, orderPlacedHandler);

        when(pipelineBehavior.handle(any(), any())).thenThrow(oops);

        GetUser     query = new GetUser(UUID.randomUUID());
        OrderPlaced evt   = new OrderPlaced(UUID.randomUUID());

        // WHEN | THEN — Request
        if (async) {
            assertThatThrownBy(() -> mediator.sendAsync(query).get(1, TimeUnit.SECONDS))
                    .hasRootCause(oops);
        } else {
            assertThatThrownBy(() -> mediator.send(query))
                    .hasRootCause(oops);
        }

        // WHEN | THEN — Notification
        if (async) {
            assertThatThrownBy(() -> mediator.publishAsync(evt).get(1, TimeUnit.SECONDS))
                    .hasRootCause(oops);
        } else {
            assertThatThrownBy(() -> mediator.publish(evt))
                    .hasRootCause(oops);
        }

        // THEN
        verifyNoInteractions(getUserHandler, orderPlacedHandler);
    }
}
