package ru.andryss.antalk.server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import ru.andryss.antalk.server.config.requestid.PropogateRequestIdTaskDecorator;
import ru.andryss.antalk.server.config.requestid.WebSocketInboundRequestIdAssignInterceptor;
import ru.andryss.antalk.server.config.requestid.WebSocketOutboundRequestIdAssignInterceptor;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketInboundRequestIdAssignInterceptor inboundRequestIdAssignInterceptor;
    private final WebSocketOutboundRequestIdAssignInterceptor outboundRequestIdAssignInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("clientInboundChannel-");
        executor.setTaskDecorator(new PropogateRequestIdTaskDecorator());
        executor.initialize();

        registration.interceptors(inboundRequestIdAssignInterceptor).taskExecutor(executor);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(outboundRequestIdAssignInterceptor);
    }
}
