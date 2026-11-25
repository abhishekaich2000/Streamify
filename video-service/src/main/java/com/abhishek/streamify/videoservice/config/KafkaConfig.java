package com.abhishek.streamify.videoservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration 
public class KafkaConfig {

    public static final String VIDEO_UPLOADED_TOPIC = "video.uploaded";
    public static final String VIDEO_ENCODED_TOPIC = "video.encoded";

    @Bean 
    public NewTopic videoUploadedTopic() {
        return TopicBuilder.name(VIDEO_UPLOADED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean 
    public NewTopic videoEncodedTopic() {
        return TopicBuilder.name(VIDEO_ENCODED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }
}
