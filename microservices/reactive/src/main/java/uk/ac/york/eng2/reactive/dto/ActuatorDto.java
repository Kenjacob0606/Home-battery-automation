package uk.ac.york.eng2.reactive.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class ActuatorDto {

    private Long id;
    private String name;
    private String type;
    private String targetState;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTargetState() { return targetState; }
    public void setTargetState(String targetState) { this.targetState = targetState; }
}

