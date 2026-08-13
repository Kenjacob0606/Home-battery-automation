package uk.ac.york.eng2.reactive.resources;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import uk.ac.york.eng2.reactive.domain.TopicSlot;
import uk.ac.york.eng2.reactive.repository.TopicSlotRepository;

import java.util.List;

@Tag(name = "topic-slots")
@Controller(TopicSlotsController.PREFIX)
public class TopicSlotsController {
    public static final String PREFIX = "/topic-slots";

    @Inject
    TopicSlotRepository topicSlotRepository;

    @Get
    public List<TopicSlot> list() {
        return topicSlotRepository.findAll();
    }
}
