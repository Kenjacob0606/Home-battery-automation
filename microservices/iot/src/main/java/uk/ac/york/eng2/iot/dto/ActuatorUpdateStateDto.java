package uk.ac.york.eng2.iot.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class ActuatorUpdateStateDto {

    private String targetState;
    public String getTargetState() { return targetState; }
    public void setTargetState(String targetState) { this.targetState = targetState; }
    }

