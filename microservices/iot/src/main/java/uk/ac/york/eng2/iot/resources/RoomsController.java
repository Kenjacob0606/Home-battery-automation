package uk.ac.york.eng2.iot.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.http.exceptions.HttpStatusException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.persistence.OneToMany;
import jakarta.transaction.Transactional;
import uk.ac.york.eng2.iot.domain.Actuator;
import uk.ac.york.eng2.iot.domain.Sensor;
import uk.ac.york.eng2.iot.dto.RoomCreateDto;
import uk.ac.york.eng2.iot.repository.ActuatorRepository;
import uk.ac.york.eng2.iot.repository.RoomRepository;
import uk.ac.york.eng2.iot.domain.Room;
import uk.ac.york.eng2.iot.repository.SensorRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Tag(name = "rooms")
@Controller(RoomsController.PREFIX)
public class RoomsController {
    public static final String PREFIX = "/rooms";


    @Inject
    RoomRepository repo;
    @Inject
    SensorRepository sensorRepository;
    @Inject
    ActuatorRepository actuatorRepository;


    @Get
    public List<Room> list(){
        return repo.findAll();
    }
    @Get("/{id}")
    @Transactional
    public Room get(@PathVariable long id){
        return repo.findById(id).orElse(null);
    }
    @Get("/{id}/sensors")
    public List<Sensor> getSensors(@PathVariable long id){
        return sensorRepository.findByRoomId(id);
    }
    @Get("/{id}/actuators")
    public List<Actuator> getActuators(@PathVariable long id){
        return actuatorRepository.findByRoomId(id);
    }

    @Post
    public HttpResponse<Void> create(@Body RoomCreateDto roomDto){
        Room room = new Room();
        room.setName(roomDto.getName());
        room = repo.save(room);
        return HttpResponse.created(URI.create("/rooms/" + room.getId()));
    }


    @Put("/{id}")
    @Transactional
    public HttpResponse<Void> update(@Body RoomCreateDto roomDto, @PathVariable long id){
        Optional<Room> oRoom = repo.findById(id);
        if(oRoom.isEmpty()){
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Room doesn't exist!");
        }
        Room room = oRoom.get();
        room.setName(roomDto.getName());

        room = repo.save(room);
        return HttpResponse.noContent();
    }

    @Delete("/{id}")
    public void delete(@PathVariable long id){
        if(repo.existsById(id)){
            repo.deleteById(id);
        }
        else{
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Cannot delete non existant room");
        }
    }

}
