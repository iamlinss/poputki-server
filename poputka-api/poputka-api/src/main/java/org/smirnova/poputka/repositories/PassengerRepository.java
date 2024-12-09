package org.smirnova.poputka.repositories;

import org.smirnova.poputka.domain.entities.PassengerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassengerRepository extends JpaRepository<PassengerEntity, Long> {
    List<PassengerEntity> findAllByUserId(Long userId);

    List<PassengerEntity> findAllByTripId(Long tripId);

    @Query("SELECT p FROM PassengerEntity p JOIN TripEntity t ON p.tripId = t.id WHERE t.user.id = :driverId")
    List<PassengerEntity> findAllByTripUser(@Param("driverId") Long driverId);
}
