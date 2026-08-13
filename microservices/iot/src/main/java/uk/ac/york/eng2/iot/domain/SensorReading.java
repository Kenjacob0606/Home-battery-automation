package uk.ac.york.eng2.iot.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;

import java.time.Instant;

@Serdeable
@Entity
public class SensorReading {

    @ManyToOne
    @JsonIgnore
    private Sensor sensor;
    public Sensor getSensor() { return sensor; }
    public void setSensor(Sensor sensor) { this.sensor = sensor; }

    @Id
    @GeneratedValue
    private Double id;
    public Double getId(){return id;}
    public void setId(Double id){this.id=id;}

    @Column
    private Long value;
    public void setValue(Long value){this.value=value;}
    public long getValue(){return value;}

    @Column
    private Instant takenAt;
    public void setTakenAt(Instant takenAt){this.takenAt = takenAt;}
    public Instant getTakenAt(){return takenAt;}
}
