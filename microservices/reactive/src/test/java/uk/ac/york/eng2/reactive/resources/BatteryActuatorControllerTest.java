package uk.ac.york.eng2.reactive.resources;

import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.york.eng2.reactive.client.IoTMClient;
import uk.ac.york.eng2.reactive.dto.ActuatorDto;
import uk.ac.york.eng2.reactive.dto.ActuatorStateDto;
import uk.ac.york.eng2.reactive.events.BatteryActuatorController;
import uk.ac.york.eng2.reactive.events.BatteryDecisionEvent;
import uk.ac.york.eng2.reactive.repository.ComponentRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@MicronautTest(transactional = false)
public class BatteryActuatorControllerTest {
    @Inject
    ComponentRepository componentRepository;
    @Inject
    IoTMClient iotmClient;
    @Inject
    BatteryActuatorController batteryActuatorController;

    @BeforeEach
    public void setup() {
        componentRepository.deleteAll();
        reset(iotmClient);
    }

    @Test
    public void updatesActuatorWithDecision() {
        ActuatorDto battery = new ActuatorDto();
        battery.setId(1L);
        battery.setType("battery");

        when(iotmClient.getActuators()).thenReturn(List.of(battery));

        batteryActuatorController.onDecision("decision",
                new BatteryDecisionEvent("import_to 100"));

        ActuatorStateDto expected = new ActuatorStateDto();
        expected.setTargetState("import_to 100");

        // only battery actuator should be updated, not heater
        verify(iotmClient).updateState(eq(1L), eq(expected));
    }

    @Test
    public void updatesMultipleBatteryActuators() {
        ActuatorDto battery1 = new ActuatorDto();
        battery1.setId(1L);
        battery1.setType("battery");

        ActuatorDto battery2 = new ActuatorDto();
        battery2.setId(2L);
        battery2.setType("battery");

        when(iotmClient.getActuators()).thenReturn(List.of(battery1, battery2));

        batteryActuatorController.onDecision("decision",
                new BatteryDecisionEvent("sell_excess"));

        ActuatorStateDto expected = new ActuatorStateDto();
        expected.setTargetState("sell_excess");

        verify(iotmClient).updateState(eq(1L), eq(expected));
        verify(iotmClient).updateState(eq(2L), eq(expected));
    }

    @Test
    public void doesNothingWhenNoActuators() {
        when(iotmClient.getActuators()).thenReturn(List.of());

        batteryActuatorController.onDecision("decision",
                new BatteryDecisionEvent("import_to 100"));

        verify(iotmClient, never()).updateState(anyLong(), any());
    }

    @Test
    public void doesNotUpdateNonBatteryActuators() {
        ActuatorDto heater = new ActuatorDto();
        heater.setId(3L);
        heater.setType("heater");

        when(iotmClient.getActuators()).thenReturn(List.of(heater));

        batteryActuatorController.onDecision("decision",
                new BatteryDecisionEvent("import_to 100"));

        verify(iotmClient, never()).updateState(anyLong(), any());
    }

    @Test
    public void updatesOnlyBatteryWhenMixedActuatorsPresent() {
        ActuatorDto battery = new ActuatorDto();
        battery.setId(1L);
        battery.setType("battery");

        ActuatorDto heater = new ActuatorDto();
        heater.setId(2L);
        heater.setType("heater");

        when(iotmClient.getActuators()).thenReturn(List.of(battery, heater));

        batteryActuatorController.onDecision("decision",
                new BatteryDecisionEvent("sell_excess"));

        ActuatorStateDto expected = new ActuatorStateDto();
        expected.setTargetState("sell_excess");

        verify(iotmClient).updateState(eq(1L), eq(expected));
        verify(iotmClient, never()).updateState(eq(2L), any());
    }


        @MockBean(IoTMClient.class)
    public IoTMClient getIoTMClient() {
        return mock(IoTMClient.class);
    }
}
