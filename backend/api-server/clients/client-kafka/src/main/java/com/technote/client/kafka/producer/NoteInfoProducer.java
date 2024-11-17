package com.technote.client.kafka.producer;

import com.technote.client.kafka.dto.NoteInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoteInfoProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void produce(NoteInfoDto noteInfoDto) {
        String topic = "video-link-events";
        kafkaTemplate.send(topic, noteInfoDto);
    }
}
