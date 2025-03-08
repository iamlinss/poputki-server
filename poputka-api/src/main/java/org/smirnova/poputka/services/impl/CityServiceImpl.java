package org.smirnova.poputka.services.impl;

import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.entities.CityEntity;
import org.smirnova.poputka.repositories.CityRepository;
import org.smirnova.poputka.services.CityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    @Override
    public List<CityEntity> findAllCities() {
        return cityRepository.findAll();
    }
}
