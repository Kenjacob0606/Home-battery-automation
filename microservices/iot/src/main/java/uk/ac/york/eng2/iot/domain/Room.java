package uk.ac.york.eng2.iot.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Serdeable
public class Room {

    @JsonIgnore
    @OneToMany(mappedBy = "room")
    private Set<Sensor> sensors;
    public Set<Sensor> getSensors(){return sensors;}
    public void setSensors(Set<Sensor>sensors){this.sensors=sensors;}

    @JsonIgnore
    @OneToMany(mappedBy = "room")
    private Set<Actuator> actuators;
    public Set<Actuator> getActuators(){return actuators;}
    public void setActuators(Set<Actuator> actuators){this.actuators=actuators;}

    @Id
    @GeneratedValue
    private Long id;
    public void setId(Long id){this.id = id;}
    public Long getId(){return id;}


    @Column
    private String name;
    public void setName(String name){this.name = name;}
    public String getName(){return name;}



}
