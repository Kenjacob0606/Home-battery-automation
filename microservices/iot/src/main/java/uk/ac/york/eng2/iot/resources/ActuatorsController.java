package uk.ac.york.eng2.iot.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.http.exceptions.HttpStatusException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.persistence.ManyToOne;
import jakarta.transaction.Transactional;
import uk.ac.york.eng2.iot.domain.Actuator;
import uk.ac.york.eng2.iot.domain.Room;
import uk.ac.york.eng2.iot.dto.ActuatorCreateDto;
import uk.ac.york.eng2.iot.dto.ActuatorUpdateStateDto;
import uk.ac.york.eng2.iot.repository.ActuatorRepository;
import uk.ac.york.eng2.iot.repository.RoomRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Tag(name = "actuators")
@Controller(ActuatorsController.PREFIX)
public class ActuatorsController {
    public static final String PREFIX = "/actuators";

    @Inject
    ActuatorRepository repo;
    @Inject
    RoomRepository roomRepository;


    @Get
    public List<Actuator> list(){
        return repo.findAll();
    }

    @Transactional
    @Get("/{id}")
    public Actuator get(@PathVariable long id) {
        return repo.findById(id).orElse(null);
    }

    @Post
    public HttpResponse<Void> create (@Body ActuatorCreateDto actuatorDto){
        Actuator actuator = new Actuator();
        actuator.setName(actuatorDto.getName());
        actuator.setType(actuatorDto.getType());
        actuator.setTargetState(actuatorDto.getTargetState());
        updateRoom(actuatorDto, actuator);
        actuator = repo.save(actuator);

        return HttpResponse.created(URI.create("/actuators/" + actuator.getId()));
    }

    protected void updateRoom(ActuatorCreateDto actuatorDto, Actuator actuator){
        if (actuatorDto.getRoomId() != null){
            Optional<Room> oRoom = roomRepository.findById(actuatorDto.getRoomId());
            if(oRoom.isEmpty()){
                throw new HttpStatusException(HttpStatus.NOT_FOUND, "Room not found");
            }
            actuator.setRoom(oRoom.get());
        }
        else {
//            actuator.setRoom(null);
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "roomId is required");
        }
    }

    @Transactional
    @Put("/{id}")
    public HttpResponse<Void> update(@Body ActuatorCreateDto actuatorDto, @PathVariable long id){
        Optional<Actuator> oActuator = repo.findById(id);
        if (oActuator.isEmpty()){
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Actuator not found");
        }
        Actuator actuator = oActuator.get();
        actuator.setName(actuatorDto.getName());
        actuator.setType(actuatorDto.getType());
        actuator.setTargetState(actuatorDto.getTargetState());
        updateRoom(actuatorDto, actuator);
        actuator =  repo.save(actuator);

        return HttpResponse.noContent();
    }

    @Patch("/{id}/state")
    @Transactional
    public HttpResponse<Void> updateState(@PathVariable long id, @Body ActuatorUpdateStateDto dto) {
        Optional<Actuator> oActuator = repo.findById(id);
        if (oActuator.isEmpty()) {
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Actuator not found");
        }
        Actuator actuator = oActuator.get();
        actuator.setTargetState(dto.getTargetState());
        repo.update(actuator);
        return HttpResponse.noContent();
    }

    @Delete("/{id}")
    public void delete(@PathVariable long id){
        if(!repo.existsById(id)){
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Cannot delete non existent actuator!");
        }
        repo.deleteById(id);
    }
}
