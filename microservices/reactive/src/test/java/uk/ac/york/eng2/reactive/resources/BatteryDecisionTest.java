package uk.ac.york.eng2.reactive.resources;


import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.york.eng2.reactive.events.*;
import uk.ac.york.eng2.reactive.repository.ComponentRepository;
import uk.ac.york.eng2.reactive.repository.TopicSlotRepository;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@MicronautTest(transactional = false)
public class BatteryDecisionTest {

    @Inject
    ComponentRepository componentRepository;
    @Inject
    TopicSlotRepository topicSlotRepository;
    @Inject
    BatteryDecision batteryDecision;
    @Inject
    BatteryDecisionProducer batteryDecisionProducer;
//    @Inject
//    IoTMClient iotmClient;

    @Inject
    BatteryDecision consumer;

    @BeforeEach
    public void setup() {
        topicSlotRepository.deleteAll();
        componentRepository.deleteAll();
        reset(batteryDecisionProducer);
    }

    @Test
    public void decidesToChargeWhenImportRateLow(){
        batteryDecision.onImportRate("import", new RateEvent(10.0));
        batteryDecision.onExportRate("export", new RateEvent(15.0));

        verify(batteryDecisionProducer).publishDecision(
                eq("decision"),
                eq(new BatteryDecisionEvent("import_to 100"))
        );
    }
    @Test
    public void decidesToSellWhenExportRateHigh(){
        batteryDecision.onImportRate("import", new RateEvent(15.0));
        batteryDecision.onExportRate("export", new RateEvent(25.0));

        verify(batteryDecisionProducer).publishDecision(
                eq("decision"),
                eq(new BatteryDecisionEvent("export_to 50"))
        );
    }
    @Test
    public void decidesToSellExcessWhenNormalRates(){
        batteryDecision.onImportRate("import", new RateEvent(20.0));
        batteryDecision.onExportRate("export", new RateEvent(5.0));

        verify(batteryDecisionProducer).publishDecision(
                eq("decision"),
                eq(new BatteryDecisionEvent("sell_excess"))
        );
    }

    @Test
    public void importPriorityWinsWhenBothConditionsTrue() {
        batteryDecision.onImportRate("import", new RateEvent(5.0));
        batteryDecision.onExportRate("export", new RateEvent(20.0));

        verify(batteryDecisionProducer).publishDecision(
                eq("decision"),
                eq(new BatteryDecisionEvent("import_to 100"))
        );
    }

    @MockBean(BatteryDecisionProducer.class)
    public BatteryDecisionProducer getBatteryDecisionProducer() {
        return mock(BatteryDecisionProducer.class);
    }


}
