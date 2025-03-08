package org.smirnova.poputka.services;

import org.smirnova.poputka.domain.entities.CityEntity;

import java.util.List;

public interface CityService {

    List<CityEntity> findAllCities();
}
