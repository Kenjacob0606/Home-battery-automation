package uk.ac.york.eng2.iot.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;

import java.util.List;

@Serdeable
@Entity
public class Sensor {


    @JsonIgnore
    @ManyToOne
    private Room room;
    public Room getRoom(){return room;}
    public void setRoom(Room room){this.room=room;}

    @JsonIgnore
    @OneToMany(mappedBy = "sensor")
    private List<SensorReading> sensorReadings;
    public void setSensorReadings(Long id){this.sensorReadings = sensorReadings;}
    public List<SensorReading> getSensorReadings(){return sensorReadings;}

    @Id
    @GeneratedValue
    private Long id;
    public void setId(Long id){this.id = id;}
    public Long getId(){return id;}



    @Column
    private String name;
    public void setName(String name){this.name = name;}
    public String getName(){return name;}

    @Column
    private String type;
    public void setType(String type){this.type = type;}
    public String getType(){return type;}

}
