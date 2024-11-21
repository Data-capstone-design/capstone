package com.technote.client.kafka.config;

import com.technote.client.kafka.dto.NoteCommentaryDto;
import com.technote.client.kafka.dto.NoteOutlineDto;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, NoteCommentaryDto> consumerCommentaryFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_1");
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        JsonDeserializer<NoteCommentaryDto> jsonDeserializer = new JsonDeserializer<>(NoteCommentaryDto.class, false);
        ErrorHandlingDeserializer<NoteCommentaryDto> errorHandlingDeserializer = new ErrorHandlingDeserializer<>(jsonDeserializer);
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), errorHandlingDeserializer);
    }

    @Bean
    public ConsumerFactory<String, NoteOutlineDto> consumerOutlineFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_1");
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        JsonDeserializer<NoteOutlineDto> jsonDeserializer = new JsonDeserializer<>(NoteOutlineDto.class, false);
        ErrorHandlingDeserializer<NoteOutlineDto> errorHandlingDeserializer = new ErrorHandlingDeserializer<>(jsonDeserializer);
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NoteCommentaryDto> kafkaCommentaryListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NoteCommentaryDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerCommentaryFactory());
        return factory;
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NoteOutlineDto> kafkaOutlineListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NoteOutlineDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerOutlineFactory());
        return factory;
    }
}