package io.github.admiralxy.mediatr.autoconfigure;

import io.github.admiralxy.mediatr.api.Mediator;
import io.github.admiralxy.mediatr.api.PipelineBehavior;
import io.github.admiralxy.mediatr.api.handler.NotificationHandler;
import io.github.admiralxy.mediatr.api.handler.RequestHandler;
import io.github.admiralxy.mediatr.core.AdaptiveExecutor;
import io.github.admiralxy.mediatr.core.MediatorImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Mediator.class)
@EnableConfigurationProperties(MediatorProperties.class)
public class MediatorAutoConfiguration {

    @Bean
    Executor mediatorExecutor(MediatorProperties props) {
        return AdaptiveExecutor.create(props.isVirtualThreads(),
                props.getCorePool(),
                props.getMaxPool(),
                props.getKeepAlive(),
                props.getQueueCapacity()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    Mediator mediator(ApplicationContext ctx, @Qualifier("mediatorExecutor") Executor ex, List<PipelineBehavior> pipeline) {
        Map<Class<?>, Object> requestHandlers = ctx.getBeansWithAnnotation(Handler.class).values().stream()
                .filter(b -> b instanceof RequestHandler)
                .flatMap(bean -> Arrays.stream(bean.getClass().getGenericInterfaces())
                        .filter(t -> t instanceof ParameterizedType)
                        .map(pt -> Map.entry(((Class<?>) ((ParameterizedType) pt).getActualTypeArguments()[0]), bean)))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
        Map<Class<?>, List<Object>> notificationHandlers = ctx.getBeansWithAnnotation(Handler.class).values().stream()
                .filter(b -> b instanceof NotificationHandler)
                .flatMap(bean -> Arrays.stream(bean.getClass().getGenericInterfaces())
                        .filter(t -> t instanceof ParameterizedType)
                        .map(pt -> Map.entry(((Class<?>) ((ParameterizedType) pt).getActualTypeArguments()[0]), bean)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        return new MediatorImpl(requestHandlers, notificationHandlers, pipeline, ex);
    }
}
