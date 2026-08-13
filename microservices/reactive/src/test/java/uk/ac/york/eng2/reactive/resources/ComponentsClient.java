package uk.ac.york.eng2.reactive.resources;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import uk.ac.york.eng2.reactive.domain.Component;

import java.util.List;

@Client(ComponentsController.PREFIX)
public interface ComponentsClient {

    @Get
    List<Component> list();
}
