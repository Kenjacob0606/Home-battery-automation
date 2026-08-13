package uk.ac.york.eng2.integration;

import io.micronaut.http.HttpResponse;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import uk.ac.york.eng2.integration.iot.api.ActuatorsApi;
import uk.ac.york.eng2.integration.iot.model.Actuator;
import uk.ac.york.eng2.integration.rcm.api.ComponentsApi;
import uk.ac.york.eng2.integration.rcm.api.TopicSlotsApi;
import uk.ac.york.eng2.integration.rcm.model.Component;
import uk.ac.york.eng2.integration.rcm.model.TopicSlot;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class BatteryWorkFlowTest {

    private static final double LOW_IMPORT_THRESHOLD = 10.0;
    private static final double HIGH_EXPORT_THRESHOLD = 15.0;

    @Inject
    private ComponentsApi componentsApi;
    @Inject
    private TopicSlotsApi topicSlotsApi;
    @Inject
    private ActuatorsApi actuatorsApi;



    @Test
    public void allExpectedComponentsAreRegistered() {
        List<Component> components = body(componentsApi.list());
        Set<String> names = components.stream().map(Component::getName).collect(Collectors.toSet());

        assertTrue(names.containsAll(Set.of(
                "AgileRatesFetcher",
                "ImportRateExtractor",
                "ExportRateExtractor",
                "BatteryDecision",
                "BatteryActuatorController"
        )), "Expected all five components to be registered in RCM, found: " + names);
    }

    @Test
    public void batteryDecisionMatchesItsOwnRecordedRatesAndPropagatesToIoTM() {
        // Wait for the pipeline to have produced a decision at least once
        await().atMost(Duration.ofSeconds(240))
                .until(() -> findSlot("battery-decision", "targetState") != null);

        TopicSlot importSlot = findSlot("current-import-rate", "rate");
        TopicSlot exportSlot = findSlot("current-export-rate", "rate");
        TopicSlot decisionSlot = findSlot("battery-decision", "targetState");

        assertNotNull(importSlot, "current-import-rate/rate slot missing");
        assertNotNull(exportSlot, "current-export-rate/rate slot missing");
        assertNotNull(importSlot.getDoubleValue());
        assertNotNull(exportSlot.getDoubleValue());

        double importRate = importSlot.getDoubleValue();
        double exportRate = exportSlot.getDoubleValue();

        String expectedState;
        if (importRate <= LOW_IMPORT_THRESHOLD) {
            expectedState = "import_to 100";
        } else if (exportRate >= HIGH_EXPORT_THRESHOLD) {
            expectedState = "export_to 50";
        } else {
            expectedState = "sell_excess";
        }

        assertEquals(expectedState, decisionSlot.getTextValue(),
                "RCM's recorded decision doesn't match its own recorded import/export rates");

        // confirm IoTM's battery actuator actually reflects that decision
        await().atMost(Duration.ofSeconds(20)).until(actuatorStateBecomes(expectedState));
    }

    private TopicSlot findSlot(String topicName, String slotName) {
        List<TopicSlot> slots = body(topicSlotsApi.list());
        return slots.stream()
                .filter(s -> topicName.equals(s.getTopicName()) && slotName.equals(s.getSlotName()))
                .findFirst()
                .orElse(null);
    }

    private Callable<Boolean> actuatorStateBecomes(String expectedState) {
        return () -> {
            List<Actuator> actuators = body(actuatorsApi.list());
            return actuators.stream()
                    .filter(a -> "battery".equals(a.getType()))
                    .anyMatch(a -> expectedState.equals(a.getTargetState()));
        };
    }

    private <T> T body(HttpResponse<T> response) {
        return response.getBody().orElseThrow(() ->
                new IllegalStateException("Empty response body from " + response));
    }
}
