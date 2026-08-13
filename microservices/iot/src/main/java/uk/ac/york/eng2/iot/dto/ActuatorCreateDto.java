package uk.ac.york.eng2.iot.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Serdeable
public class ActuatorCreateDto {

//    private long id;
//    public Long getId(){return id;}

    private Long roomId;
    public void setRoomId(Long roomId){this.roomId=roomId;}
    public Long getRoomId(){return roomId;}

    private String name;
    public void setName(String name){this.name=name;}
    public String getName(){return name;}

    private String type;
    public void setType(String type){this.type=type;}
    public String getType(){return type;}

    private String targetState;
    public void setTargetState(String targetState){this.targetState = targetState;}
    public String getTargetState(){return targetState;}
}
