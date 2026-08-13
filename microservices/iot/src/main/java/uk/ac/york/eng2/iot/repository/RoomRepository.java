package uk.ac.york.eng2.iot.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import uk.ac.york.eng2.iot.domain.Room;

import java.util.Optional;

@Repository
public interface RoomRepository extends CrudRepository<Room,Long> {
    Optional<Room> findByName(String name);
}
