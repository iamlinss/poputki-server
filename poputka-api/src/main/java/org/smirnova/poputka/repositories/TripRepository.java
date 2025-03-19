package org.smirnova.poputka.repositories;

import org.smirnova.poputka.domain.entities.TripEntity;
import org.smirnova.poputka.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<TripEntity, Long> {
    List<TripEntity> findAllByUser(UserEntity user);
}
