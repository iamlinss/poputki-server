package org.smirnova.poputka.services;

import org.smirnova.poputka.domain.entities.CityEntity;

import java.util.List;
import java.util.Optional;

public interface CityService {

    List<CityEntity> findAllCities();

    CityEntity createCity(CityEntity cityEntity);

    Optional<CityEntity> updateCity(Long id, CityEntity cityEntity);

    boolean deleteCity(Long id);
}
