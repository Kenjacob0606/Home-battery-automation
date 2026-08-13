package uk.ac.york.eng2.reactive.events;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient
public interface BatteryDecisionProducer {

    @Topic(AgileRatesTopics.TOPIC_BATTERY_DECISION)
    void publishDecision(@KafkaKey String key, BatteryDecisionEvent event);
}
