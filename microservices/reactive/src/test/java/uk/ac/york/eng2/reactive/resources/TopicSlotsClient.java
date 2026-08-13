package uk.ac.york.eng2.reactive.resources;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import uk.ac.york.eng2.reactive.domain.TopicSlot;

import java.util.List;

@Client(TopicSlotsController.PREFIX)
public interface TopicSlotsClient {

    @Get
    List<TopicSlot> list();
}
