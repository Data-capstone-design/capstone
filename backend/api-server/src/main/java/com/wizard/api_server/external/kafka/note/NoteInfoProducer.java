package com.wizard.api_server.external.kafka.note;

import com.wizard.api_server.domain.event.DomainEvent;
import com.wizard.api_server.domain.note.event.NoteEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoteInfoProducer implements NoteEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(DomainEvent event) {
        String topic = "video-link-events";
        log.info("Publishing event {}", event);
        kafkaTemplate.send(topic, event);
    }
}
