package org.bratanov.poputka.services;

import jdk.jshell.Snippet;
import org.bratanov.poputka.domain.entities.CityEntity;
import org.bratanov.poputka.domain.entities.StatusEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DataService {

    List<CityEntity> findAllCities();

    List<StatusEntity> findAllStatuses();
}
