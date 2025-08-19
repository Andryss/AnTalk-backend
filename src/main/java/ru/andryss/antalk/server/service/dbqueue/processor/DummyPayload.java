package ru.andryss.antalk.server.service.dbqueue.processor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.andryss.antalk.server.service.dbqueue.QueuePayload;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DummyPayload extends QueuePayload {
    public static final String QUEUE_NAME = "DUMMY_PAYLOAD";

    private String data;
}
