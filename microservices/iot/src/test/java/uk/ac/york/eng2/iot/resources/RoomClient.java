package uk.ac.york.eng2.iot.resources;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import uk.ac.york.eng2.iot.domain.Actuator;
import uk.ac.york.eng2.iot.domain.Room;
import uk.ac.york.eng2.iot.domain.Sensor;
import uk.ac.york.eng2.iot.dto.RoomCreateDto;
import uk.ac.york.eng2.iot.repository.ActuatorRepository;
import uk.ac.york.eng2.iot.repository.RoomRepository;
import uk.ac.york.eng2.iot.repository.SensorRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Client("/rooms")
public interface RoomClient {

        @Get
        public List<Room> list();

        @Get("/{id}")
        public Room get(@PathVariable long id);

        @Get("/{id}/sensors")
        public List<Sensor> getRoomSensors(@PathVariable long id);

        @Get("/{id}/actuators")
        public List<Actuator> getRoomActuators(@PathVariable long id);

        @Post
        public HttpResponse<Void> create(@Body RoomCreateDto roomDto);

        @Put("/{id}")
        public HttpResponse<Void> update(@Body RoomCreateDto roomDto, @PathVariable long id);

        @Delete("/{id}")
        HttpResponse delete(@PathVariable long id);
}
