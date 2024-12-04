package org.smirnova.poputka.repositories;

import org.smirnova.poputka.domain.entities.PassengerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<PassengerEntity, Long> {
    List<PassengerEntity> findAllByUserId(Long userId);

    List<PassengerEntity> findAllByTripId(Long tripId);

    Optional<PassengerEntity> findByIdAndTripId(Long id, Long tripId);
}
