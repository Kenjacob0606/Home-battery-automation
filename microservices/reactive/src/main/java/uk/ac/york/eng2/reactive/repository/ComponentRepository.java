package uk.ac.york.eng2.reactive.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import uk.ac.york.eng2.reactive.domain.Component;

import java.util.Optional;

@Repository
public interface ComponentRepository extends CrudRepository<Component, Long> {

    Optional<Component> findByName(String name);

}
