package org.smirnova.poputka.services;

import org.smirnova.poputka.domain.entities.CarEntity;
import org.smirnova.poputka.domain.entities.UserEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface CarService {

    CarEntity save(CarEntity carEntity, UserEntity userEntity);

    Optional<CarEntity> findOne(Long id);

    boolean isExists(Long id);

    void remove(CarEntity carEntity);

    Optional<Double> calculatePrice(String cityFrom, String cityTo);
}
