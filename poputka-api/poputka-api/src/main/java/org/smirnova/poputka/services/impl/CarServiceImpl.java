package org.smirnova.poputka.services.impl;

import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.entities.CarEntity;
import org.smirnova.poputka.domain.entities.UserEntity;
import org.smirnova.poputka.repositories.CarRepository;
import org.smirnova.poputka.repositories.UserRepository;
import org.smirnova.poputka.services.CarService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;


    @Override
    public CarEntity save(CarEntity carEntity, UserEntity userEntity) {
        CarEntity savedCar = carRepository.save(carEntity);
        List<CarEntity> carEntityList = userEntity.getCars();
        carEntityList.add(savedCar);
        userEntity.setCars(carEntityList);
        userRepository.save(userEntity);
        return savedCar;

    }

    @Override
    public List<CarEntity> findAll() {
        return StreamSupport.stream(carRepository.findAll().spliterator(), false).toList();
    }

    @Override
    public Optional<CarEntity> findOne(Long id) {
        return carRepository.findById(id);
    }

    @Override
    public boolean isExists(Long id) {
        return carRepository.existsById(id);
    }

    @Override
    public void remove(CarEntity carEntity) {

        carRepository.delete(carEntity);
    }
}
