package org.smirnova.poputka.services.impl;

import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.entities.CarEntity;
import org.smirnova.poputka.domain.entities.UserEntity;
import org.smirnova.poputka.repositories.CarRepository;
import org.smirnova.poputka.repositories.UserRepository;
import org.smirnova.poputka.services.CarService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Override
    public Optional<Double> calculatePrice(String cityFrom, String cityTo) {
        Map<String, Double> cityCoefficients = new HashMap<>();
        cityCoefficients.put("Москва", 1.5);
        cityCoefficients.put("Санкт-Петербург", 1.3);
        cityCoefficients.put("Екатеринбург", 1.1);
        cityCoefficients.put("Новосибирск", 1.0);
        cityCoefficients.put("Казань", 1.2);
        cityCoefficients.put("Челябинск", 1.05);
        cityCoefficients.put("Нижний Новгород", 1.1);
        cityCoefficients.put("Самара", 1.0);
        cityCoefficients.put("Ростов-на-Дону", 1.0);
        cityCoefficients.put("Уфа", 1.07);
        cityCoefficients.put("Воронеж", 1.0);
        cityCoefficients.put("Красноярск", 1.05);
        cityCoefficients.put("Пермь", 1.0);
        cityCoefficients.put("Тюмень", 1.08);
        cityCoefficients.put("Саранск", 0.95);
        cityCoefficients.put("Ижевск", 0.95);
        cityCoefficients.put("Хабаровск", 1.1);
        cityCoefficients.put("Владивосток", 1.12);
        cityCoefficients.put("Ярославль", 1.0);
        cityCoefficients.put("Тула", 1.0);
        cityCoefficients.put("Минск", 1.5);
        cityCoefficients.put("Гомель", 1.2);
        cityCoefficients.put("Могилёв", 1.1);
        cityCoefficients.put("Гродно", 1.1);
        cityCoefficients.put("Брест", 1.0);
        cityCoefficients.put("Витебск", 1.0);
        cityCoefficients.put("Орша", 0.95);
        cityCoefficients.put("Барановичи", 0.95);
        cityCoefficients.put("Слуцк", 0.9);
        cityCoefficients.put("Жлобин", 0.95);

        Double fromCoef = cityCoefficients.get(cityFrom);
        Double toCoef = cityCoefficients.get(cityTo);

        if (fromCoef == null || toCoef == null) {
            return Optional.empty(); // Если хотя бы один город не найден
        }

        double basePrice = 20.0; // Базовая цена
        double averageCityCoef = (fromCoef + toCoef) / 2;

        return Optional.of(Math.round(basePrice * averageCityCoef * 100.0) / 100.0);
    }
}
