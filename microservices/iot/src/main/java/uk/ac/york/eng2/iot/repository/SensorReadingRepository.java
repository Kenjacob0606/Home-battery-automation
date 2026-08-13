package uk.ac.york.eng2.iot.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import uk.ac.york.eng2.iot.domain.SensorReading;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorReadingRepository extends CrudRepository<SensorReading, Long> {
    List<SensorReading> findBySensorId(Long sensorId);
    List<SensorReading> findBySensorIdOrderByTakenAtDesc(Long sensorId);
}
