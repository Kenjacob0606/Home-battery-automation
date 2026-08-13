package uk.ac.york.eng2.reactive.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class ActuatorStateDto {

    private String targetState;
    public String getTargetState() { return targetState; }
    public void setTargetState(String targetState) { this.targetState = targetState; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActuatorStateDto)) return false;
        ActuatorStateDto that = (ActuatorStateDto) o;
        return java.util.Objects.equals(targetState, that.targetState);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(targetState);
    }

}
