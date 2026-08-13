package uk.ac.york.eng2.iot.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;

@Entity
@Serdeable
public class Actuator {
    @JsonIgnore
    @ManyToOne
    private Room room;
    public Room getRoom(){return room;}
    public void setRoom(Room room){this.room=room;}

    @Id
    @GeneratedValue
    private Long id;
    public void setId(Long id){this.id=id;}
    public Long getId(){return id;}

    @Column
    private String name;
    public void setName(String name){this.name=name;}
    public String getName(){return name;}

    @Column
    private String type;
    public void setType(String type){this.type=type;}
    public String getType(){return type;}

    @Column
    private String targetState;
    public void setTargetState(String targetState){this.targetState = targetState;}
    public String getTargetState(){return targetState;}
}
