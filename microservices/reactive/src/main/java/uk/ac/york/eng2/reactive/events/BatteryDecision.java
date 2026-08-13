package uk.ac.york.eng2.reactive.events;

import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import uk.ac.york.eng2.reactive.domain.Component;
import uk.ac.york.eng2.reactive.domain.TopicSlot;
import uk.ac.york.eng2.reactive.repository.ComponentRepository;
import uk.ac.york.eng2.reactive.repository.TopicSlotRepository;

@KafkaListener(groupId = "battery-controller", offsetReset = OffsetReset.EARLIEST)
public class BatteryDecision {
    public static final String COMPONENT_NAME = "BatteryDecision";
    public static final double LOW_IMPORT_THRESHOLD = 10.0;
    public static final double HIGH_EXPORT_THRESHOLD = 15.0;
    private final Object lock = new Object();

    @Inject
    ComponentRepository componentRepository;
    @Inject
    TopicSlotRepository topicSlotRepository;
    @Inject
    BatteryDecisionProducer batteryDecisionProducer;


    @PostConstruct
    public void init() {
        if (componentRepository.findByName(COMPONENT_NAME).isEmpty()) {
            Component c = new Component();
            c.setName(COMPONENT_NAME);
            componentRepository.save(c);
        }
    }


    @Topic(AgileRatesTopics.TOPIC_CURRENT_IMPORT_RATE)
    public void onImportRate(@KafkaKey String key, RateEvent event){
        synchronized (lock) {
            handleImportRate(key, event);

        }
    }


    @Topic(AgileRatesTopics.TOPIC_CURRENT_EXPORT_RATE)
    public void onExportRate(@KafkaKey String key, RateEvent event) {
        synchronized (lock) {
            handleExportRate(key, event);

        }
    }

    private void saveSlot(String topicName, Double rate) {
        TopicSlot slot = topicSlotRepository
                .findByTopicNameAndSlotName(topicName, "rate")
                .orElse(new TopicSlot());
        slot.setTopicName(topicName);
        slot.setSlotName("rate");
        slot.setSlotType("double");
        slot.setDoubleValue(rate);
        topicSlotRepository.save(slot);
    }

    @Transactional
    protected void handleImportRate(String key, RateEvent event) {
        saveSlot(AgileRatesTopics.TOPIC_CURRENT_IMPORT_RATE, event.rate());
        decide();
    }

    @Transactional
    protected void handleExportRate(String key, RateEvent event) {
        saveSlot(AgileRatesTopics.TOPIC_CURRENT_EXPORT_RATE, event.rate());
        decide();
    }

    private void decide() {
        System.out.println("BATTERY CONTROLLER TRIGGERED");
        var importSlot = topicSlotRepository
                .findByTopicNameAndSlotName(AgileRatesTopics.TOPIC_CURRENT_IMPORT_RATE, "rate");
        var exportSlot = topicSlotRepository
                .findByTopicNameAndSlotName(AgileRatesTopics.TOPIC_CURRENT_EXPORT_RATE, "rate");

        if (importSlot.isEmpty() || exportSlot.isEmpty()) return;

        double importRate = importSlot.get().getDoubleValue();
        double exportRate = exportSlot.get().getDoubleValue();

        String targetState;
        if (importRate <= LOW_IMPORT_THRESHOLD) {
            targetState = "import_to 100";
        } else if (exportRate >= HIGH_EXPORT_THRESHOLD) {
            targetState = "export_to 50";
        } else {
            targetState = "sell_excess";
        }

        // save decision slot
        TopicSlot slot = topicSlotRepository
                .findByTopicNameAndSlotName(AgileRatesTopics.TOPIC_BATTERY_DECISION, "targetState")
                .orElse(new TopicSlot());
        slot.setTopicName(AgileRatesTopics.TOPIC_BATTERY_DECISION);
        slot.setSlotName("targetState");
        slot.setSlotType("text");
        slot.setTextValue(targetState);

        topicSlotRepository.save(slot);

        batteryDecisionProducer.publishDecision("decision", new BatteryDecisionEvent(targetState));
        System.out.println("BATTERY DECISION PUBLISHED: " + targetState);

    }
}
