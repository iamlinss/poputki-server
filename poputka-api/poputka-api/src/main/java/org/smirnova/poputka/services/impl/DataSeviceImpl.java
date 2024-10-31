package org.smirnova.poputka.services.impl;

import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.entities.CityEntity;
import org.smirnova.poputka.domain.entities.StatusEntity;
import org.smirnova.poputka.repositories.CityRepository;
import org.smirnova.poputka.repositories.StatusRepository;
import org.smirnova.poputka.services.DataService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class DataSeviceImpl implements DataService {

    private final CityRepository cityRepository;
    private final StatusRepository statusRepository;

    @Override
    public List<CityEntity> findAllCities() {
        return StreamSupport.stream(cityRepository.findAll().spliterator(),false).toList();
    }

    @Override
    public List<StatusEntity> findAllStatuses() {
        return StreamSupport.stream(statusRepository.findAll().spliterator(),false).toList();
    }
}
