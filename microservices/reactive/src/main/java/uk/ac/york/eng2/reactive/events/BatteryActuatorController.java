package uk.ac.york.eng2.reactive.events;

import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import uk.ac.york.eng2.reactive.client.IoTMClient;
import uk.ac.york.eng2.reactive.domain.Component;
import uk.ac.york.eng2.reactive.dto.ActuatorDto;
import uk.ac.york.eng2.reactive.dto.ActuatorStateDto;
import uk.ac.york.eng2.reactive.repository.ComponentRepository;

import java.util.List;

@KafkaListener(groupId = "battery-actuator-controller")
public class BatteryActuatorController {
    public static final String COMPONENT_NAME = "BatteryActuatorController";

    @Inject
    ComponentRepository componentRepository;
    @Inject
    IoTMClient ioTMClient;

    @PostConstruct
    public void init() {
        if (componentRepository.findByName(COMPONENT_NAME).isEmpty()) {
            Component c = new Component();
            c.setName(COMPONENT_NAME);
            componentRepository.save(c);
        }
    }

    @Transactional
    @Topic(AgileRatesTopics.TOPIC_BATTERY_DECISION)
    public void onDecision(@KafkaKey String key, BatteryDecisionEvent event) {
        System.out.println("ACTUATOR CONTROLLER TRIGGERED: " + event.targetState());
        ActuatorStateDto stateDto = new ActuatorStateDto();
        stateDto.setTargetState(event.targetState());

        List<ActuatorDto> actuators = ioTMClient.getActuators();
        for (ActuatorDto actuator : actuators) {
            if ("battery".equals(actuator.getType())) {
                ioTMClient.updateState(actuator.getId(), stateDto);
            }
        }
    }
}


