package uk.ac.york.eng2.reactive.resources;

import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.york.eng2.reactive.client.IoTMClient;
import uk.ac.york.eng2.reactive.events.*;
import uk.ac.york.eng2.reactive.repository.ComponentRepository;
import uk.ac.york.eng2.reactive.repository.TopicSlotRepository;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@MicronautTest(transactional = false)
public class ImportRateExtractorTest {

    @Inject
    ComponentRepository componentRepository;
    @Inject
    TopicSlotRepository topicSlotRepository;

    @Inject
    ImportRateExtractor consumer;
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
        consumer.init();
        AgileRatesEvent event = createTestEvent(5.0, 25.0);
        consumer.onAgileRates("rates", event);
        assertTrue(componentRepository.findByName(ImportRateExtractor.COMPONENT_NAME).isPresent());
    }


    @Test
    public void publishesImportRate() {
        AgileRatesEvent event = createTestEvent(5.0, 25.0);
        consumer.onAgileRates("rates", event);
        verify(rateProducer).publishImportRate(eq("import"), eq(new RateEvent(5.0)));
    }
    @Test
    public void savesImportRateSlot(){
        AgileRatesEvent event = createTestEvent(5.0, 25.0);
        consumer.onAgileRates("rates", event);

        var slot = topicSlotRepository.findByTopicNameAndSlotName(AgileRatesTopics.TOPIC_CURRENT_IMPORT_RATE, "rate");
        assertTrue(slot.isPresent());
        assertEquals(5.0, slot.get().getDoubleValue());
    }

    @Test
    public void updatesImportRateSlot(){
        AgileRatesEvent event1 = createTestEvent(5.0, 25.0);
        consumer.onAgileRates("rates", event1);

        AgileRatesEvent event2 = createTestEvent(9.0, 25.0);
        consumer.onAgileRates("rates", event2);

        var slot = topicSlotRepository.findByTopicNameAndSlotName(AgileRatesTopics.TOPIC_CURRENT_IMPORT_RATE, "rate");
        assertEquals(9.0, slot.get().getDoubleValue());

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

        var slot = topicSlotRepository.findByTopicNameAndSlotName(AgileRatesTopics.TOPIC_CURRENT_IMPORT_RATE, "rate");
        assertTrue(slot.isEmpty());
        verify(rateProducer, never()).publishImportRate(any(), any());
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
