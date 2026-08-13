package uk.ac.york.eng2.reactive.events;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient
public interface AgileRatesProducer {

    @Topic(AgileRatesTopics.TOPIC_AGILE_RATES)
    void publishAgileRates(@KafkaKey String key, AgileRatesEvent event);

}
