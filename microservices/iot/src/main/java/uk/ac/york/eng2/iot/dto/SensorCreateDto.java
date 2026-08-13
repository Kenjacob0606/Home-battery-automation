package uk.ac.york.eng2.iot.dto;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;
import uk.ac.york.eng2.iot.domain.Sensor;
import uk.ac.york.eng2.iot.repository.SensorRepository;

import java.util.List;
import java.util.Optional;

@Serdeable
public class SensorCreateDto {
//    private Long id;
//    public long getId(){return id;}

    private String name;
    public void setName(String name){this.name = name;}
    public String getName(){return name;}

    private String type;
    public void setType(String type){this.type = type;}
    public String getType(){return type;}

    private Long roomId;
    public void setRoomId(Long roomId){this.roomId = roomId;}
    public Long getRoomId(){return roomId;}

}
