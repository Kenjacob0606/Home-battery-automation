package uk.ac.york.eng2.reactive.resources;

import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.york.eng2.reactive.events.*;
import uk.ac.york.eng2.reactive.repository.ComponentRepository;
import uk.ac.york.eng2.reactive.repository.TopicSlotRepository;
import static org.junit.jupiter.api.Assertions.*;


import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@MicronautTest(transactional = false)
public class ExportRateExtractorTest {

    @Inject
    ExportRateExtractor consumer;

    @Inject
    TopicSlotRepository topicSlotRepository;

    @Inject
    ComponentRepository componentRepository;

    @Inject
    RateProducer rateProducer;

    @BeforeEach
    public void setup() {
        topicSlotRepository.deleteAll();
        componentRepository.deleteAll();
        reset(rateProducer);
    }

    @Test
    public void savesComponentOnFirstEvent() {
        consumer.init();                //initialise since deleteall() deletes the rows
        AgileRatesEvent event = createTestEvent(5.0, 25.0);
        consumer.onAgileRates("rate", event);
        assertTrue(componentRepository.findByName(ExportRateExtractor.COMPONENT_NAME).isPresent());
    }

    @Test
    public void publishesExportRate() {
        AgileRatesEvent event = createTestEvent(5.0, 25.0);
        consumer.onAgileRates("rates", event);
        verify(rateProducer).publishExportRate(eq("export"), eq(new RateEvent(25.0)));
    }

    @Test
    public void savesExportRateSlot(){
        AgileRatesEvent event = createTestEvent(5.0, 25.0);
        consumer.onAgileRates("rate", event);

        var slot = topicSlotRepository
                .findByTopicNameAndSlotName(AgileRatesTopics.TOPIC_CURRENT_EXPORT_RATE, "rate");
        assertTrue(slot.isPresent());
        assertEquals(25.0, slot.get().getDoubleValue());

    }

    @Test
    public void updatesExportRateSlot() {
        AgileRatesEvent event1 = createTestEvent(5.0, 25.0);
        consumer.onAgileRates("rate", event1);

        AgileRatesEvent event2 = createTestEvent(5.0, 30.0);
        consumer.onAgileRates("rate", event2);

        var slot = topicSlotRepository
                .findByTopicNameAndSlotName(AgileRatesTopics.TOPIC_CURRENT_EXPORT_RATE, "rate");
        assertEquals(30.0, slot.get().getDoubleValue());
    }

    @Test
    public void doesNotPublishWhenAgileRateResultMissing() {
        Instant now = Instant.now();
        OffsetDateTime start = now.minusSeconds(900).atOffset(ZoneOffset.UTC);
        OffsetDateTime end = now.plusSeconds(900).atOffset(ZoneOffset.UTC);

        // slot covers "now" but has no agileRate at all
        String json = String.format("""
            {
                "result": "ok",
                "rates": [{
                    "deliveryStart": "%s",
                    "deliveryEnd": "%s"
                }]
            }
            """, start, end);

        AgileRatesEvent event = new AgileRatesEvent(json, now.minusSeconds(900), now.plusSeconds(900));
        consumer.onAgileRates("rates", event);

        var slot = topicSlotRepository.findByTopicNameAndSlotName(AgileRatesTopics.TOPIC_CURRENT_EXPORT_RATE, "rate");
        assertTrue(slot.isEmpty());
        verify(rateProducer, never()).publishExportRate(any(), any());
    }

    private AgileRatesEvent createTestEvent(double importRate, double exportRate){
        Instant now = Instant.now();
        OffsetDateTime start = now.minusSeconds(900).atOffset(ZoneOffset.UTC);
        OffsetDateTime end = now.plusSeconds(900).atOffset(ZoneOffset.UTC);

        String json = String.format("""
            {
                "result": "ok",
                "rates": [{
                    "deliveryStart": "%s",
                    "deliveryEnd": "%s",
                    "agileRate": {"result": {"rate": %s}},
                    "agileOutgoingRate": {"result": {"rate": %s}}
                }]
            }
            """, start, end, importRate, exportRate);

        return new AgileRatesEvent(json, now.minusSeconds(900), now.plusSeconds(900));
    }

    @MockBean(RateProducer.class)
    public RateProducer getRateProducer() {
        return mock(RateProducer.class);
    }
}
