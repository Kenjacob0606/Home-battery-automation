package uk.ac.york.eng2.iot.resources;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.exceptions.HttpStatusException;
import uk.ac.york.eng2.iot.domain.Actuator;
import uk.ac.york.eng2.iot.domain.Room;
import uk.ac.york.eng2.iot.dto.ActuatorCreateDto;
import uk.ac.york.eng2.iot.dto.ActuatorUpdateStateDto;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Client("/actuators")
public interface ActuatorClient {
    @Get
    public List<Actuator> list();

    @Get("/{id}")
    public Actuator get(@PathVariable long id) ;

    @Post
    public HttpResponse<Void> create (@Body ActuatorCreateDto actuatorDto);

    @Put("/{id}")
    public HttpResponse<Void> update(@Body ActuatorCreateDto actuatorDto, @PathVariable long id);

    @Patch("/{id}/state")
    HttpResponse<Void> updateState(@PathVariable long id, @Body ActuatorUpdateStateDto dto);

    @Delete("/{id}")
    HttpResponse delete(@PathVariable long id);

}
