package uk.ac.york.eng2.reactive.events;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record BatteryDecisionEvent(String targetState) {
}
