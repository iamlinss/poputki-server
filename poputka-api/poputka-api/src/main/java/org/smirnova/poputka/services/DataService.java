package org.smirnova.poputka.services;

import jdk.jshell.Snippet;
import org.smirnova.poputka.domain.entities.CityEntity;
import org.smirnova.poputka.domain.entities.StatusEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DataService {

    List<CityEntity> findAllCities();

    List<StatusEntity> findAllStatuses();
}
