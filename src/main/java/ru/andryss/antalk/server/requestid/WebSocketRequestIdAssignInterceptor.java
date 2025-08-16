package ru.andryss.antalk.server.requestid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@SuppressWarnings("NullableProblems")
public class WebSocketRequestIdAssignInterceptor extends AbstractRequestIdAssign implements ChannelInterceptor {

    private static final String MESSAGE_TYPE_HEADER = "simpMessageType";
    private static final String DESTINATION_HEADER = "simpDestination";
    private static final String NO_HEADER_VALUE = "???";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        assignRequestId();

        MessageHeaders headers = message.getHeaders();
        log.info("Incoming WS request: {} {}", headers.getOrDefault(MESSAGE_TYPE_HEADER, NO_HEADER_VALUE),
                headers.getOrDefault(DESTINATION_HEADER, NO_HEADER_VALUE));

        return message;
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        clearRequestId();
    }
}
