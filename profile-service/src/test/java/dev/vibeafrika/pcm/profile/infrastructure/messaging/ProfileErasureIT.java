package dev.vibeafrika.pcm.profile.infrastructure.messaging;

import dev.vibeafrika.pcm.profile.application.usecase.EraseProfileUseCase;
import dev.vibeafrika.pcm.profile.domain.model.Handle;
import dev.vibeafrika.pcm.profile.domain.model.Profile;
import dev.vibeafrika.pcm.profile.domain.repository.ProfileRepository;
import dev.vibeafrika.pcm.common.domain.TenantId;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class ProfileErasureIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    @Autowired
    private EraseProfileUseCase eraseProfileUseCase;

    @Autowired
    private ProfileRepository profileRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Test
    void shouldPublishProfileErasedEventWhenProfileIsErased() {
        // Arrange
        String tenantId = "test-tenant";
        Profile profile = Profile.create(TenantId.of(tenantId), Handle.of("test_user"),
                Map.of("email", "test@example.com"));
        profileRepository.save(profile);
        UUID profileId = profile.getId();

        // Setup Kafka consumer to verify event
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()); // Simplification:
                                                                                                       // using String
                                                                                                       // for check
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("profile-events"));

        // Act
        eraseProfileUseCase.execute(new EraseProfileUseCase.Input(tenantId, profileId));

        // Assert
        // Verify database state (Soft Delete)
        Profile erasedProfile = profileRepository.findById(profileId).orElseThrow();
        assertThat(erasedProfile.getDeletedAt()).isNotNull();
        assertThat(erasedProfile.getAttributes()).isEmpty();
        assertThat(erasedProfile.getHandle().getValue()).startsWith("anonymized_");

        // Verify Kafka event
        ConsumerRecord<String, String> record = consumer.poll(Duration.ofSeconds(10)).iterator().next();
        assertThat(record.key()).isEqualTo(profileId.toString());
        assertThat(record.value()).contains("ProfileErased");

        consumer.close();
    }
}
