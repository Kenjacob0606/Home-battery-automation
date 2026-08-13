package uk.ac.york.eng2.iot.resources;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.york.eng2.iot.domain.Actuator;
import uk.ac.york.eng2.iot.domain.Room;
import uk.ac.york.eng2.iot.dto.ActuatorCreateDto;
import uk.ac.york.eng2.iot.dto.RoomCreateDto;
import uk.ac.york.eng2.iot.dto.SensorCreateDto;
import uk.ac.york.eng2.iot.repository.ActuatorRepository;
import uk.ac.york.eng2.iot.repository.RoomRepository;
import uk.ac.york.eng2.iot.repository.SensorRepository;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(transactional = false)
public class ActuatorControllerTest {

    @Inject
    ActuatorRepository repo;
    @Inject
    RoomRepository roomRepository;
    @Inject
    SensorRepository sensorRepository;
    @Inject
    ActuatorClient client;

    @BeforeEach
    public void setup(){
        sensorRepository.deleteAll();
        repo.deleteAll();
        roomRepository.deleteAll();
    }

    @Test
    public void noActuators(){
        assertEquals(0, client.list().size());
    }

    @Test
    public void listActuators(){
        Room room = new Room();
        room.setName("room 1");
        room = roomRepository.save(room);

        ActuatorCreateDto actuatorDto = new ActuatorCreateDto();
        actuatorDto.setName("actuator 1");
        actuatorDto.setType("type 1");
        actuatorDto.setTargetState("state 1");
        actuatorDto.setRoomId(room.getId());
        client.create(actuatorDto);

        ActuatorCreateDto actuator_2 = new ActuatorCreateDto();
        actuator_2.setName("actuator 2");
        actuator_2.setType("type 2");
        actuator_2.setTargetState("state 1");
        actuator_2.setRoomId(room.getId());
        client.create(actuator_2);

        assertEquals(2, client.list().size());
    }

    @Test
    public void get(){
        Room room = new Room();
        room.setName("room 1");
        room = roomRepository.save(room);

        ActuatorCreateDto actuatorDto = new ActuatorCreateDto();
        actuatorDto.setName("actuator 1");
        actuatorDto.setType("type 1");
        actuatorDto.setTargetState("state 1");
        actuatorDto.setRoomId(room.getId());
        Long actuatorId = createActuator(actuatorDto);
        Actuator actuator = client.get(actuatorId);

        assertEquals(actuatorDto.getName(), actuator.getName());    //actuator names are unique, name testing is sufficient
    }

    @Test
    public void getNonExistantActuator(){assertNull(client.get(345));}

    @Test
    public void create(){
        Room room = new Room();
        room.setName("room 1");
        room = roomRepository.save(room);

        ActuatorCreateDto actuatorDto = new ActuatorCreateDto();
        actuatorDto.setName("actuator 1");
        actuatorDto.setType("type 1");
        actuatorDto.setTargetState("state 1");
        actuatorDto.setRoomId(room.getId());
        client.create(actuatorDto);

        assertEquals(1, client.list().size());
    }

    @Test
    public void create_roomDoesNotExist(){
        ActuatorCreateDto actuatorDto = new ActuatorCreateDto();
        actuatorDto.setName("actuator 1");
        actuatorDto.setType("type 1");
        actuatorDto.setTargetState("state 1");
        actuatorDto.setRoomId(999L);
        HttpResponse<Void> createResponse = client.create(actuatorDto);
        assertEquals(HttpStatus.NOT_FOUND, createResponse.getStatus());
    }

    @Test
    public void update(){
        Room room = new Room();
        room.setName("room 1");
        room = roomRepository.save(room);

        ActuatorCreateDto actuatorDto = new ActuatorCreateDto();
        actuatorDto.setName("actuator 1");
        actuatorDto.setType("type 1");
        actuatorDto.setTargetState("state 1");
        actuatorDto.setRoomId(room.getId());

        Long actuatorId = createActuator(actuatorDto);
        actuatorDto.setName("actuator 2");
        client.update(actuatorDto, actuatorId);
        Actuator updatedActuator = client.get(actuatorId);

        assertEquals(actuatorDto.getName(), updatedActuator.getName());

    }

    @Test
    public void update_notFound(){
        ActuatorCreateDto actuatorDto = new ActuatorCreateDto();
        actuatorDto.setName("actuator 1");
        actuatorDto.setType("type 1");
        actuatorDto.setTargetState("state 1");
        actuatorDto.setRoomId(1L);

        HttpResponse<Void> updateResponse = client.create(actuatorDto);
        assertEquals(HttpStatus.NOT_FOUND, updateResponse.getStatus());
    }

    @Test
    public void delete(){
        Room room = new Room();
        room.setName("room 1");
        room = roomRepository.save(room);

        ActuatorCreateDto actuatorDto = new ActuatorCreateDto();
        actuatorDto.setName("actuator 1");
        actuatorDto.setType("type 1");
        actuatorDto.setTargetState("state 1");
        actuatorDto.setRoomId(room.getId());

        Long actuatorId = createActuator(actuatorDto);
        client.delete(actuatorId);

        assertEquals(0, client.list().size());
    }

    @Test
    public void deleteNonExistantRoom(){
        HttpResponse<Void> deleteResponse = client.delete(243);
        assertEquals(HttpStatus.NOT_FOUND, deleteResponse.getStatus());
    }

    private Long createActuator(ActuatorCreateDto actuatorDto){
        HttpResponse<Void> createResponse = client.create(actuatorDto);
        Long actuatorId = Long.valueOf(createResponse.header(HttpHeaders.LOCATION).split("/")[2]);
        return actuatorId;
    }
}
