package com.gazi.gazi_renew.common.config;



import com.gazi.gazi_renew.notification.domain.dto.NotificationCreate;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;


import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    @Value("${kafka.url}")
    private String kafkaServerUrl;
    @Bean(name = "notificationListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, NotificationCreate> notificationListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NotificationCreate> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(notificationConsumerFactory());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, NotificationCreate> notificationConsumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "gazi");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(config);
    }

}

