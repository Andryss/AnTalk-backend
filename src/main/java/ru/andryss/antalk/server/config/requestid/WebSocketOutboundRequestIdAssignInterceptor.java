package ru.andryss.antalk.server.config.requestid;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.DESTINATION_HEADER;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.MESSAGE_TYPE_HEADER;

@Slf4j
@Component
@SuppressWarnings("NullableProblems")
public class WebSocketOutboundRequestIdAssignInterceptor extends BaseWebSocketInterceptor implements RequestIdAware {

    @Override
    public Message<?> preSendInternal(Message<?> message, MessageChannel channel, SimpMessageHeaderAccessor accessor) {
        String requestId = fetchRequestId();

        if (!StringUtils.isBlank(requestId) && accessor.isMutable()) {
            accessor.setNativeHeader(REQUEST_ID_HEADER, requestId);
        }

        MessageHeaders headers = message.getHeaders();
        log.info("Outbound WS message: {} {}", headers.getOrDefault(MESSAGE_TYPE_HEADER, NO_MESSAGE_TYPE),
                headers.getOrDefault(DESTINATION_HEADER, NO_DESTINATION));

        return message;
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        clearRequestId();
    }
}
