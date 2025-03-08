package org.smirnova.poputka.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smirnova.poputka.domain.entities.CarEntity;
import org.smirnova.poputka.domain.entities.UserEntity;
import org.smirnova.poputka.repositories.CarRepository;
import org.smirnova.poputka.repositories.UserRepository;
import org.smirnova.poputka.services.impl.CarServiceImpl;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CarServiceImpl carService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveCar() {
        CarEntity car = new CarEntity();
        car.setId(1L);

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setCars(new ArrayList<>());

        when(carRepository.save(car)).thenReturn(car);
        when(userRepository.save(user)).thenReturn(user);

        CarEntity result = carService.save(car, user);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(carRepository, times(1)).save(car);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findOne_ShouldReturnOptionalCar_WhenCarExists() {
        CarEntity car = new CarEntity();
        car.setId(1L);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        Optional<CarEntity> result = carService.findOne(1L);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(car);
        verify(carRepository, times(1)).findById(1L);
    }

    @Test
    void findOne_ShouldReturnEmptyOptional_WhenCarDoesNotExist() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<CarEntity> result = carService.findOne(1L);

        assertThat(result).isEmpty();
        verify(carRepository, times(1)).findById(1L);
    }

    @Test
    void isExists_ShouldReturnTrue_WhenCarExists() {
        when(carRepository.existsById(1L)).thenReturn(true);

        boolean result = carService.isExists(1L);

        assertThat(result).isTrue();
        verify(carRepository, times(1)).existsById(1L);
    }

    @Test
    void isExists_ShouldReturnFalse_WhenCarDoesNotExist() {
        when(carRepository.existsById(1L)).thenReturn(false);

        boolean result = carService.isExists(1L);

        assertThat(result).isFalse();
        verify(carRepository, times(1)).existsById(1L);
    }

    @Test
    void remove_ShouldDeleteCar() {
        CarEntity car = new CarEntity();

        doNothing().when(carRepository).delete(car);

        carService.remove(car);

        verify(carRepository, times(1)).delete(car);
    }

    @Test
    void calculatePrice_ShouldReturnPrice_WhenBothCitiesExist() {
        String cityFrom = "Москва";
        String cityTo = "Санкт-Петербург";

        Optional<Double> result = carService.calculatePrice(cityFrom, cityTo);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(28); // (20 * (1.5 + 1.3) / 2)
    }

    @Test
    void calculatePrice_ShouldReturnEmptyOptional_WhenCityDoesNotExist() {
        String cityFrom = "Неизвестный город";
        String cityTo = "Санкт-Петербург";

        Optional<Double> result = carService.calculatePrice(cityFrom, cityTo);

        assertThat(result).isEmpty();
    }
}
