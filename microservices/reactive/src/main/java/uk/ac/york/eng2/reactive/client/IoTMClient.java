package uk.ac.york.eng2.reactive.client;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.annotation.Client;
import uk.ac.york.eng2.reactive.dto.ActuatorDto;
import uk.ac.york.eng2.reactive.dto.ActuatorStateDto;

import java.util.List;

@Client("${iotm.url}")
public interface IoTMClient {

    @Get("/actuators")
    List<ActuatorDto> getActuators();

    @Patch("/actuators/{id}/state")
    HttpResponse<Void> updateState(@PathVariable long id, @Body ActuatorStateDto dto);
}
