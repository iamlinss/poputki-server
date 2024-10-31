package org.smirnova.poputka.services;

import org.smirnova.poputka.domain.entities.CarEntity;
import org.smirnova.poputka.domain.entities.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CarService {

    CarEntity save(CarEntity carEntity, UserEntity userEntity);

    List<CarEntity> findAll();

    Optional<CarEntity> findOne(Long id);

    boolean isExists(Long id);

    void remove(CarEntity carEntity);
}
