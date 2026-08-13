package uk.ac.york.eng2.iot.resources;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import uk.ac.york.eng2.iot.domain.SensorReading;
import uk.ac.york.eng2.iot.repository.SensorReadingRepository;

import java.util.List;


@Tag(name = "sensorReadings")
@Controller(SensorReadingsController.PREFIX)
public class SensorReadingsController {
    public static final String PREFIX = "/sensorReadings";

    @Inject
    SensorReadingRepository sensorReadingRepository;

    @Get("/{id}/readings")
    public List<SensorReading> getReadings(@PathVariable long id) {
        return sensorReadingRepository.findBySensorIdOrderByTakenAtDesc(id);
    }
}
