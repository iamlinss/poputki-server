package org.smirnova.poputka.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smirnova.poputka.domain.entities.CityEntity;
import org.smirnova.poputka.repositories.CityRepository;
import org.smirnova.poputka.services.impl.CityServiceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CityServiceImplTest {

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CityServiceImpl cityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllCities_ShouldReturnListOfCities() {
        CityEntity city1 = new CityEntity();
        city1.setCity("Москва");
        CityEntity city2 = new CityEntity();
        city2.setCity("Санкт-Петербург");

        when(cityRepository.findAll()).thenReturn(List.of(city1, city2));

        List<CityEntity> result = cityService.findAllCities();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(city1, city2);
        verify(cityRepository, times(1)).findAll();
    }

    @Test
    void findAllCities_ShouldReturnEmptyList_WhenNoCitiesExist() {
        when(cityRepository.findAll()).thenReturn(List.of());

        List<CityEntity> result = cityService.findAllCities();

        assertThat(result).isEmpty();
        verify(cityRepository, times(1)).findAll();
    }
}
