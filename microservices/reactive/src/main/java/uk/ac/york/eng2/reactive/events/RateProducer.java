package uk.ac.york.eng2.reactive.events;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient
public interface RateProducer {

    @Topic(AgileRatesTopics.TOPIC_CURRENT_IMPORT_RATE)
    void publishImportRate(@KafkaKey String key, RateEvent event);

    @Topic(AgileRatesTopics.TOPIC_CURRENT_EXPORT_RATE)
    void publishExportRate(@KafkaKey String key, RateEvent event);

}
