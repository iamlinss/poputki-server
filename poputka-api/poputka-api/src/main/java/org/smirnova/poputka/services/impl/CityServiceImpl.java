package org.smirnova.poputka.services.impl;

import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.entities.CityEntity;
import org.smirnova.poputka.repositories.CityRepository;
import org.smirnova.poputka.services.CityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    @Override
    public List<CityEntity> findAllCities() {
        return cityRepository.findAll();
    }

    @Override
    public CityEntity createCity(CityEntity cityEntity) {
        return cityRepository.save(cityEntity);
    }

    @Override
    public Optional<CityEntity> updateCity(Long id, CityEntity cityEntity) {
        return cityRepository.findById(id)
                .map(existingCity -> {
                    existingCity.setCity(cityEntity.getCity());
                    existingCity.setCountry(cityEntity.getCountry());
                    return cityRepository.save(existingCity);
                });
    }

    @Override
    public boolean deleteCity(Long id) {
        if (cityRepository.existsById(id)) {
            cityRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
