package com.gazi.gazi_renew.common.config;

import com.google.common.collect.ImmutableMap;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import com.gazi.gazi_renew.notification.domain.dto.NotificationCreate;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaProducerConfig {

    @Value("${kafka.url}")
    private String kafkaServerUrl;

    @Bean
    public Map<String, Object> producerConfigurations() {
        return ImmutableMap.<String, Object>builder()
                .put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                .put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
                .put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class)
                .build();
    }
    @Bean
    public ProducerFactory<String, NotificationCreate> notificationProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigurations());

    }
    @Bean
    public KafkaTemplate<String, NotificationCreate> notificationKafkaTemplate() {
        return new KafkaTemplate<>(notificationProducerFactory());
    }
}
