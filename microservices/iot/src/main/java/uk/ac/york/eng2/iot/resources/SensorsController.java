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
import uk.ac.york.eng2.iot.domain.Room;
import uk.ac.york.eng2.iot.domain.Sensor;
import uk.ac.york.eng2.iot.dto.SensorCreateDto;
import uk.ac.york.eng2.iot.repository.RoomRepository;
import uk.ac.york.eng2.iot.repository.SensorReadingRepository;
import uk.ac.york.eng2.iot.repository.SensorRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Tag(name = "sensors")
@Controller(SensorsController.PREFIX)
public class SensorsController {
    public static final String PREFIX = "/sensors";


    @Inject
    SensorRepository repo;
    @Inject
    RoomRepository roomRepository;
    @Inject
    SensorReadingRepository sensorReadingRepository;

    @Get
    public List<Sensor> list(){
        return repo.findAll();
    }

    @Get("/{id}")
    @Transactional
    public Sensor get(@PathVariable long id){
        return repo.findById(id).orElse(null);
    }

    @Post
    public HttpResponse<Void> create(@Body SensorCreateDto sensorDto){
        Sensor sensor = new Sensor();
        sensor.setName(sensorDto.getName());
        sensor.setType(sensorDto.getType());
        updateRoom(sensorDto, sensor);
        sensor = repo.save(sensor);
        return HttpResponse.created(URI.create("/sensors/" + sensor.getId()));
    }
    protected void updateRoom(SensorCreateDto sensorDto, Sensor sensor){
        if(sensorDto.getRoomId() != null) {
            Optional<Room> oRoom = roomRepository.findById(sensorDto.getRoomId());
            if (oRoom.isEmpty()) {
                throw new HttpStatusException(HttpStatus.NOT_FOUND, "Room not found");
            }
            sensor.setRoom(oRoom.get());
        }
        else {
//            sensor.setRoom(null);
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "roomId is required");
        }
    }

    @Put("/{id}")
    @Transactional
    public HttpResponse<Void> update(@Body SensorCreateDto sensorDto, @PathVariable long id){
        Optional<Sensor> oSensor = repo.findById(id);
        if (oSensor.isEmpty()){
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Sensor does not exist");
        }
        Sensor sensor = oSensor.get();
        sensor.setName(sensorDto.getName());
        sensor.setType(sensorDto.getType());
        updateRoom(sensorDto,sensor);
        sensor = repo.save(sensor);

        return HttpResponse.noContent();
    }

    @Delete("/{id}")
    public void delete(@PathVariable long id){
            if(!repo.existsById(id)) {
                throw new HttpStatusException(HttpStatus.NOT_FOUND, " Cannot delete non existant sensor!");
            }
            repo.deleteById(id);
    }
}
