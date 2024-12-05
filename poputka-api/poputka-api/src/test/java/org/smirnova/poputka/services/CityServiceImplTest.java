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
import java.util.Optional;

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

    @Test
    void createCity_ShouldSaveCity() {
        CityEntity city = new CityEntity();
        city.setCity("Минск");

        when(cityRepository.save(city)).thenReturn(city);

        CityEntity result = cityService.createCity(city);

        assertThat(result).isEqualTo(city);
        verify(cityRepository, times(1)).save(city);
    }

    @Test
    void updateCity_ShouldUpdateCity_WhenCityExists() {
        Long cityId = 1L;
        CityEntity existingCity = new CityEntity();
        existingCity.setCity("Старый город");
        existingCity.setCountry("Старая страна");

        CityEntity updatedCity = new CityEntity();
        updatedCity.setCity("Новый город");
        updatedCity.setCountry("Новая страна");

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(existingCity));
        when(cityRepository.save(existingCity)).thenReturn(existingCity);

        Optional<CityEntity> result = cityService.updateCity(cityId, updatedCity);

        assertThat(result).isPresent();
        assertThat(result.get().getCity()).isEqualTo("Новый город");
        assertThat(result.get().getCountry()).isEqualTo("Новая страна");
        verify(cityRepository, times(1)).findById(cityId);
        verify(cityRepository, times(1)).save(existingCity);
    }

    @Test
    void updateCity_ShouldReturnEmptyOptional_WhenCityDoesNotExist() {
        Long cityId = 1L;
        CityEntity updatedCity = new CityEntity();

        when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

        Optional<CityEntity> result = cityService.updateCity(cityId, updatedCity);

        assertThat(result).isEmpty();
        verify(cityRepository, times(1)).findById(cityId);
        verify(cityRepository, never()).save(any(CityEntity.class));
    }

    @Test
    void deleteCity_ShouldReturnTrue_WhenCityExists() {
        Long cityId = 1L;

        when(cityRepository.existsById(cityId)).thenReturn(true);
        doNothing().when(cityRepository).deleteById(cityId);

        boolean result = cityService.deleteCity(cityId);

        assertThat(result).isTrue();
        verify(cityRepository, times(1)).existsById(cityId);
        verify(cityRepository, times(1)).deleteById(cityId);
    }

    @Test
    void deleteCity_ShouldReturnFalse_WhenCityDoesNotExist() {
        Long cityId = 1L;

        when(cityRepository.existsById(cityId)).thenReturn(false);

        boolean result = cityService.deleteCity(cityId);

        assertThat(result).isFalse();
        verify(cityRepository, times(1)).existsById(cityId);
        verify(cityRepository, never()).deleteById(cityId);
    }
}
