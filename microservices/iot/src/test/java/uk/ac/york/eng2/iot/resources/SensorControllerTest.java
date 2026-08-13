package uk.ac.york.eng2.iot.resources;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.york.eng2.iot.domain.Room;
import uk.ac.york.eng2.iot.domain.Sensor;
import uk.ac.york.eng2.iot.dto.RoomCreateDto;
import uk.ac.york.eng2.iot.dto.SensorCreateDto;
import uk.ac.york.eng2.iot.repository.ActuatorRepository;
import uk.ac.york.eng2.iot.repository.RoomRepository;
import uk.ac.york.eng2.iot.repository.SensorRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(transactional = false)
public class SensorControllerTest {

    @Inject
    SensorRepository repo;
    @Inject
    RoomRepository roomRepository;
    @Inject
    ActuatorRepository actuatorRepository;
    @Inject
    SensorClient client;

    @BeforeEach
    public void setup(){
        repo.deleteAll();
        actuatorRepository.deleteAll();
        roomRepository.deleteAll();
    }

    @Test
    public void noSensors(){
        assertEquals(0, client.list().size());
    }

    @Test
    public void list(){
        Room room = new Room();
        room.setName("room 1");
        room = roomRepository.save(room);

        SensorCreateDto sensorDto = new SensorCreateDto();
        sensorDto.setName("Sensor 1");
        sensorDto.setType("Temperature");
        sensorDto.setRoomId(room.getId());
        client.create(sensorDto);

        SensorCreateDto sensorDto_2 = new SensorCreateDto();
        sensorDto_2.setName("Sensor 2");
        sensorDto_2.setType("light");
        sensorDto_2.setRoomId(room.getId());
        client.create(sensorDto_2);

        List<Sensor> sensors = client.list();
        assertEquals(2, sensors.size());
    }

    @Test
    public void get(){
        Room room = new Room();
        room.setName("room 1");
        room = roomRepository.save(room);

        SensorCreateDto sensorDto = new SensorCreateDto();
        sensorDto.setName("Sensor 1");
        sensorDto.setType("Temperature");
        sensorDto.setRoomId(room.getId());
        Long sensorId = createSensor(sensorDto);
        Sensor sensor = client.get(sensorId);

        assertEquals(sensorDto.getName(), sensor.getName());    //sensors can't have same names, so testing for name equivalence is sufficient
    }
    @Test
    public void getNonExistantSensor(){assertNull(client.get(345));}

    @Test
    public void create(){
        Room room = new Room();
        room.setName("room 1");
        room = roomRepository.save(room);

        SensorCreateDto sensorDto = new SensorCreateDto();
        sensorDto.setName("Sensor 1");
        sensorDto.setType("Temperature");
        sensorDto.setRoomId(room.getId());
        client.create(sensorDto);

        assertEquals(1, client.list().size());
    }

    @Test
    public void createSensorInNonExistantRoom(){
        SensorCreateDto sensorDto = new SensorCreateDto();
        sensorDto.setName("Sensor 1");
        sensorDto.setType("Temperature");
        sensorDto.setRoomId(352L);
        HttpResponse <Void> createResponse = client.create(sensorDto);
        assertEquals(HttpStatus.NOT_FOUND, createResponse.getStatus());
    }

    @Test
    public void update(){
        Room room = new Room();
        room.setName("room 1");
        room = roomRepository.save(room);

        SensorCreateDto sensorDto = new SensorCreateDto();
        sensorDto.setName("Sensor 1");
        sensorDto.setType("Temperature");
        sensorDto.setRoomId(room.getId());
        Long sensorId = createSensor(sensorDto);
        sensorDto.setName("Sensor 2");
        client.update(sensorDto, sensorId);
        Sensor updatedSensor = client.get(sensorId);

        assertEquals(sensorDto.getName(), updatedSensor.getName());
    }

    @Test
    public void updateNonExistantSensor(){
        Room room = new Room();
        room.setName("room 1");
        room = roomRepository.save(room);

        SensorCreateDto sensorDto = new SensorCreateDto();
        sensorDto.setName("Sensor 1");
        sensorDto.setType("Temperature");
        sensorDto.setRoomId(room.getId());

        HttpResponse<Void> updateResponse = client.update(sensorDto, 87L);
        assertEquals(HttpStatus.NOT_FOUND, updateResponse.getStatus());
    }

    @Test
    public void delete(){
        Room room = new Room();
        room.setName("room 1");
        room = roomRepository.save(room);

        SensorCreateDto sensorDto = new SensorCreateDto();
        sensorDto.setName("Sensor 1");
        sensorDto.setType("Temperature");
        sensorDto.setRoomId(room.getId());
        Long sensorId = createSensor(sensorDto);

        client.delete(sensorId);
        assertEquals(0, client.list().size());
    }

    @Test
    public void deleteNonExistantRoom(){
        HttpResponse deleteResponse = client.delete(243);
        assertEquals(HttpStatus.NOT_FOUND, deleteResponse.getStatus());
    }

    private Long createSensor(SensorCreateDto sensorDto){
        HttpResponse<Void> createResponse = client.create(sensorDto);
        Long sensorId = Long.valueOf(createResponse.header(HttpHeaders.LOCATION).split("/")[2]);
        return sensorId;
    }
}
