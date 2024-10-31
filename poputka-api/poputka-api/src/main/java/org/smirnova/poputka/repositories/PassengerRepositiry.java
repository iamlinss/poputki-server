package org.bratanov.poputka.repositories;

import org.bratanov.poputka.domain.entities.PassengerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassengerRepositiry extends JpaRepository<PassengerEntity, Long> {
    List<PassengerEntity> findAllByUserId(Long userId);
}
