package uk.ac.york.eng2.reactive.resources;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import uk.ac.york.eng2.reactive.domain.Component;
import uk.ac.york.eng2.reactive.repository.ComponentRepository;

import java.util.List;

@Tag(name = "components")
@Controller (ComponentsController.PREFIX)
public class ComponentsController {
    public static final String PREFIX = "/components";
    @Inject
    ComponentRepository componentRepository;

    @Get
    public List<Component> list() {
        return componentRepository.findAll();
    }

}
