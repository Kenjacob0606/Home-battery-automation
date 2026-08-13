package uk.ac.york.eng2.iot.resources;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.york.eng2.iot.domain.Actuator;
import uk.ac.york.eng2.iot.domain.Room;
import uk.ac.york.eng2.iot.domain.Sensor;
import uk.ac.york.eng2.iot.dto.RoomCreateDto;
import uk.ac.york.eng2.iot.dto.SensorCreateDto;
import uk.ac.york.eng2.iot.repository.ActuatorRepository;
import uk.ac.york.eng2.iot.repository.RoomRepository;
import uk.ac.york.eng2.iot.repository.SensorRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(transactional = false)
public class RoomControllerTest {

    @Inject
    RoomRepository repo;
    @Inject
    SensorRepository sensorRepository;
    @Inject
    ActuatorRepository actuatorRepository;

    @Inject
    RoomClient client;

    @BeforeEach
    public void setup(){
        sensorRepository.deleteAll();
        actuatorRepository.deleteAll();
        repo.deleteAll();
    }

    @Test
    public void noRooms(){
        assertEquals(0, client.list().size());
    }

    @Test
    public void listRooms(){
        RoomCreateDto roomDto = new RoomCreateDto();
        roomDto.setName("Living Room");
        client.create(roomDto);
        RoomCreateDto roomDto_2 = new RoomCreateDto();
        roomDto_2.setName("Bed room");
        client.create(roomDto_2);

        assertEquals(2, client.list().size());
    }

    @Test
    public void listRoomSensors(){
        RoomCreateDto roomDto = new RoomCreateDto();
        roomDto.setName("Living room");
        Long roomId = createRoom(roomDto);

        Sensor sensor = new Sensor();
        sensor.setName("sensor1");
        sensor.setType("Type a");
        sensor.setRoom(repo.findById(roomId).get());
        sensorRepository.save(sensor);

        List<Sensor> roomSensors =client.getRoomSensors(roomId);

        assertEquals(1, roomSensors.size());
    }

    @Test
    public void listRoomActuators(){
        RoomCreateDto roomDto = new RoomCreateDto();
        roomDto.setName("Living room");
        long roomId = createRoom(roomDto);

        Actuator actuator = new Actuator();
        actuator.setName("sensor1");
        actuator.setType("Type a");
        actuator.setTargetState("state 1");
        actuator.setRoom(repo.findById(roomId).get());
        actuatorRepository.save(actuator);

        List<Actuator> roomActuators =client.getRoomActuators(roomId);

        assertEquals(1, roomActuators.size());
    }

    @Test
    public void getRoom(){
        RoomCreateDto roomDto = new RoomCreateDto();
        roomDto.setName("Living Room");
        Long roomId = createRoom(roomDto);
        Room fetchedRoom =  client.get(roomId);

        assertEquals(roomDto.getName(), fetchedRoom.getName());
    }

    @Test
    public void getNonExistantRoom(){assertNull(client.get(345));}

    @Test
    public void createRoom(){
        RoomCreateDto roomDto = new RoomCreateDto();
        roomDto.setName("Living Room");
        client.create(roomDto);
        assertEquals(1, client.list().size());
    }

    @Test
    public void updateRoom(){
        RoomCreateDto roomDto = new RoomCreateDto();
        roomDto.setName("Living Room");
        Long roomId = createRoom(roomDto);
        roomDto.setName("Bed Room");
        client.update(roomDto,roomId);
        Room updatedRoom = client.get(roomId);

        assertEquals(roomDto.getName(), updatedRoom.getName());
    }

    @Test
    public void updateNonExistantRoom(){
        RoomCreateDto roomDto = new RoomCreateDto();
        roomDto.setName("room2");
        HttpResponse updateResponse = client.update(roomDto, 243);
        assertEquals(HttpStatus.NOT_FOUND, updateResponse.getStatus());
    }

    private Long createRoom(RoomCreateDto roomDto){
        HttpResponse<Void> createResponse = client.create(roomDto);
        Long roomId = Long.valueOf(createResponse.header(HttpHeaders.LOCATION).split("/")[2]);
        return roomId;
    }

    @Test
    public void deleteRoom(){
        RoomCreateDto roomDto = new RoomCreateDto();
        roomDto.setName("Bed Room");
        Long roomId = createRoom(roomDto);
        client.delete(roomId);

        assertEquals(0, client.list().size());

    }

    @Test
    public void deleteNonExistantRoom(){
        HttpResponse deleteResponse = client.delete(243);
        assertEquals(HttpStatus.NOT_FOUND, deleteResponse.getStatus());
    }
}
