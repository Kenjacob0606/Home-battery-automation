package uk.ac.york.eng2.reactive.events;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

@Requires(bean = AdminClient.class)
@Factory
public class AgileRatesTopicFactory {

    @Bean
    public NewTopic agileRatesTopic() {
        return new NewTopic(AgileRatesTopics.TOPIC_AGILE_RATES, 1, (short) 1);
    }

    @Bean
    public NewTopic currentImportRateTopic() {
        return new NewTopic(AgileRatesTopics.TOPIC_CURRENT_IMPORT_RATE, 1, (short) 1);
    }

    @Bean
    public NewTopic currentExportRateTopic() {
        return new NewTopic(AgileRatesTopics.TOPIC_CURRENT_EXPORT_RATE, 1, (short) 1);
    }

    @Bean
    public NewTopic batteryDecisionTopic(){
        return new NewTopic(AgileRatesTopics.TOPIC_BATTERY_DECISION, 1, (short)1);
    }

}
