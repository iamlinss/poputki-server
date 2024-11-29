package org.smirnova.poputka.repositories;

import org.smirnova.poputka.domain.entities.CityEntity;
import org.smirnova.poputka.domain.entities.TripEntity;
import org.smirnova.poputka.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<TripEntity, Long> {

    @Query(value = "SELECT T FROM TripEntity T WHERE" +
            " (:departure IS NULL OR T.departureLocation=:departure) AND" +
            " (:destination IS NULL OR T.destinationLocation = :destination) AND" +
            " (:seats = 0 OR T.seats<=:seats)")
    List<TripEntity> findAllByFilter(@Param("departure") CityEntity departure,
                                     @Param("destination") CityEntity destination,
                                     @Param("seats") int seats);

    List<TripEntity> findAllByUser(UserEntity user);
}
