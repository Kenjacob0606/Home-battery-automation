package uk.ac.york.eng2.reactive.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import uk.ac.york.eng2.reactive.domain.TopicSlot;

import java.util.Optional;

@Repository
public interface TopicSlotRepository extends CrudRepository<TopicSlot, Long> {

    Optional<TopicSlot> findByTopicNameAndSlotName(String topicName, String slotName);

}
