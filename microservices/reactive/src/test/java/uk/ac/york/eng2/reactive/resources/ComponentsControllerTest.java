package uk.ac.york.eng2.reactive.resources;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.york.eng2.reactive.domain.Component;
import uk.ac.york.eng2.reactive.repository.ComponentRepository;
import uk.ac.york.eng2.reactive.repository.TopicSlotRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@MicronautTest(transactional = false)
public class ComponentsControllerTest {

    @Inject
    ComponentRepository componentRepository;

    @Inject
    TopicSlotRepository topicSlotRepository;

    @Inject
    ComponentsClient client;

    @BeforeEach
    public void setup() {
        topicSlotRepository.deleteAll();
        componentRepository.deleteAll();
    }

    @Test
    public void noComponents() {
        assertEquals(0, client.list().size());
    }

    @Test
    public void listComponents() {
        Component c1 = new Component();
        c1.setName("AgileRatesFetcher");
        componentRepository.save(c1);

        Component c2 = new Component();
        c2.setName("BatteryDecision");
        componentRepository.save(c2);

        List<Component> components = client.list();
        assertEquals(2, components.size());
    }

}
