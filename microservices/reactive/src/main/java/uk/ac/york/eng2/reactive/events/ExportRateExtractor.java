package uk.ac.york.eng2.reactive.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.micronaut.scheduling.annotation.Scheduled;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import uk.ac.york.cs.eng2.reactive.rates.model.AgileRatesResponse;
import uk.ac.york.cs.eng2.reactive.rates.model.AgileSlotRates;
import uk.ac.york.eng2.reactive.domain.Component;
import uk.ac.york.eng2.reactive.domain.TopicSlot;
import uk.ac.york.eng2.reactive.repository.ComponentRepository;
import uk.ac.york.eng2.reactive.repository.TopicSlotRepository;

import java.time.Instant;

@KafkaListener(groupId = "export-rate-extractor", offsetReset = OffsetReset.EARLIEST)
public class ExportRateExtractor {
    public static final String COMPONENT_NAME = "ExportRateExtractor";
    public static final String SLOT_NAME = "rate";
    private AgileRatesEvent lastEvent = null;


    @Inject
    ComponentRepository componentRepository;
    @Inject
    TopicSlotRepository topicSlotRepository;
    @Inject
    RateProducer rateProducer;
    @Inject
    ObjectMapper objectMapper;


    @PostConstruct
    public void init() {
        if (componentRepository.findByName(COMPONENT_NAME).isEmpty()) {
            Component c = new Component();
            c.setName(COMPONENT_NAME);
            componentRepository.save(c);
        }
    }

    @Transactional
    @Topic(AgileRatesTopics.TOPIC_AGILE_RATES)
    public void onAgileRates(@KafkaKey String key, AgileRatesEvent event) {
        System.out.println("EXPORT EXTRACTOR TRIGGERED");
        this.lastEvent = event;
        extractAndPublish(event);
    }

    @Scheduled(fixedDelay = "30m", initialDelay = "30m")
    @Transactional
    public void scheduledExtract() {
        System.out.println("EXPORT EXTRACTOR SCHEDULED TRIGGERED");
        if (lastEvent == null) return;
        extractAndPublish(lastEvent);
    }

    private void extractAndPublish(AgileRatesEvent event) {
        try {
            AgileRatesResponse response = objectMapper.readValue(event.json(), AgileRatesResponse.class);
            Instant now = Instant.now();

            Double currentRate = null;
            for (AgileSlotRates slot : response.getRates()) {
                if (slot.getDeliveryStart() == null || slot.getDeliveryEnd() == null) continue;
                Instant start = slot.getDeliveryStart().toInstant();
                Instant end = slot.getDeliveryEnd().toInstant();
                if (!now.isBefore(start) && now.isBefore(end)) {
                    if (slot.getAgileOutgoingRate() != null && slot.getAgileOutgoingRate().getResult() != null) {
                        currentRate = slot.getAgileOutgoingRate().getResult().getRate();
                    }
                    break;
                }
            }
            if (currentRate == null) return;

            TopicSlot slot = topicSlotRepository
                    .findByTopicNameAndSlotName(AgileRatesTopics.TOPIC_CURRENT_EXPORT_RATE, SLOT_NAME)
                    .orElse(new TopicSlot());
            slot.setTopicName(AgileRatesTopics.TOPIC_CURRENT_EXPORT_RATE);
            slot.setSlotName(SLOT_NAME);
            slot.setSlotType("double");
            slot.setDoubleValue(currentRate);
            topicSlotRepository.save(slot);

            rateProducer.publishExportRate("export", new RateEvent(currentRate));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
