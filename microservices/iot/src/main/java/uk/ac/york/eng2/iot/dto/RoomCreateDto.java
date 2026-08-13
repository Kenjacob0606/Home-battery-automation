package uk.ac.york.eng2.iot.dto;

import io.micronaut.serde.annotation.Serdeable;
import uk.ac.york.eng2.iot.domain.Actuator;
import uk.ac.york.eng2.iot.domain.Sensor;

import java.util.Set;

@Serdeable
public class RoomCreateDto {

//    private Long id;
//    public Long  getId(){return id;}

    private String name;
    public String getName(){return name;}
    public void setName(String name){this.name=name;}


}
