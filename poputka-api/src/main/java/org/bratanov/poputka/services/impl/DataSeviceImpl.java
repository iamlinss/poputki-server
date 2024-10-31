package org.bratanov.poputka.services.impl;

import lombok.RequiredArgsConstructor;
import org.bratanov.poputka.domain.entities.CityEntity;
import org.bratanov.poputka.domain.entities.StatusEntity;
import org.bratanov.poputka.repositories.CityRepository;
import org.bratanov.poputka.repositories.StatusRepository;
import org.bratanov.poputka.services.DataService;
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
