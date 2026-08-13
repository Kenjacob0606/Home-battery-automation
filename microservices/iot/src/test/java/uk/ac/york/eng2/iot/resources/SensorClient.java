package uk.ac.york.eng2.iot.resources;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.transaction.Transactional;
import uk.ac.york.eng2.iot.domain.Room;
import uk.ac.york.eng2.iot.domain.Sensor;
import uk.ac.york.eng2.iot.dto.SensorCreateDto;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Client("/sensors")
public interface SensorClient {

    @Get
    public List<Sensor> list();

    @Get("/{id}")
    public Sensor get(@PathVariable long id);

    @Post
    public HttpResponse<Void> create(@Body SensorCreateDto sensorDto);

    @Put("/{id}")
    public HttpResponse<Void> update(@Body SensorCreateDto sensorDto, @PathVariable long id);

    @Delete("/{id}")
   HttpResponse delete(@PathVariable long id);

}
