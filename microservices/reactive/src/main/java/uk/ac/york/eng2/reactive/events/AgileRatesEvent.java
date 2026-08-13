package uk.ac.york.eng2.reactive.events;

import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;

@Serdeable
public record AgileRatesEvent(String json, Instant validFrom, Instant validTo) {}
