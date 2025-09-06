package ru.andryss.antalk.server.config.requestid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@SuppressWarnings("NullableProblems")
public abstract class BaseWebSocketInterceptor implements ChannelInterceptor {

    protected static final String NO_MESSAGE_TYPE = "<no-message-type>";
    protected static final String NO_DESTINATION = "<no-destination>";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        SimpMessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message, SimpMessageHeaderAccessor.class
        );

        return preSendInternal(message, channel, accessor);
    }

    /**
     * Вызов метода аналогичен preSend, но с передачей класса для доступа к заголовкам сообщения.
     */
    public abstract Message<?> preSendInternal(
            Message<?> message,
            MessageChannel channel,
            SimpMessageHeaderAccessor accessor
    );
}
