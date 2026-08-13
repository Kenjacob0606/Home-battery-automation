package uk.ac.york.eng2.reactive.resources;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.york.eng2.reactive.domain.TopicSlot;
import uk.ac.york.eng2.reactive.repository.ComponentRepository;
import uk.ac.york.eng2.reactive.repository.TopicSlotRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(transactional = false)
public class TopicSlotsControllerTest {

    @Inject
    TopicSlotRepository topicSlotRepository;

    @Inject
    ComponentRepository componentRepository;

    @Inject
    TopicSlotsClient client;

    @BeforeEach
    public void setup() {
        topicSlotRepository.deleteAll();
        componentRepository.deleteAll();
    }

    @Test
    public void noTopicSlots() {
        assertEquals(0, client.list().size());
    }

    @Test
    public void listTopicSlots() {
        TopicSlot slot1 = new TopicSlot();
        slot1.setTopicName("current-import-rate");
        slot1.setSlotName("rate");
        slot1.setSlotType("double");
        slot1.setDoubleValue(5.0);
        topicSlotRepository.save(slot1);

        TopicSlot slot2 = new TopicSlot();
        slot2.setTopicName("current-export-rate");
        slot2.setSlotName("rate");
        slot2.setSlotType("double");
        slot2.setDoubleValue(25.0);
        topicSlotRepository.save(slot2);

        List<TopicSlot> slots = client.list();
        assertEquals(2, slots.size());
    }

}
