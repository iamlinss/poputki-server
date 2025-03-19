package org.smirnova.poputka.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smirnova.poputka.domain.dto.trip.TripFilterDto;
import org.smirnova.poputka.domain.entities.*;
import org.smirnova.poputka.domain.enums.TripStatus;
import org.smirnova.poputka.repositories.CityRepository;
import org.smirnova.poputka.repositories.PassengerRepository;
import org.smirnova.poputka.repositories.TripRepository;
import org.smirnova.poputka.services.impl.EmailServiceImpl;
import org.smirnova.poputka.services.impl.TripServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TripServiceImplTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private EmailServiceImpl emailService;

    @InjectMocks
    private TripServiceImpl tripService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateStatus_ShouldUpdateStatus_WhenTripExistsAndStatusValid() {
        Long tripId = 1L;
        TripEntity trip = new TripEntity();
        trip.setId(tripId);
        trip.setStatus(TripStatus.CREATED);

        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
        when(tripRepository.save(trip)).thenReturn(trip);

        tripService.updateStatus(tripId, TripStatus.COMPLETED);

        assertThat(trip.getStatus()).isEqualTo(TripStatus.COMPLETED);
        verify(tripRepository, times(1)).findById(tripId);
        verify(tripRepository, times(1)).save(trip);
    }

    @Test
    void updateStatus_ShouldThrowException_WhenTripNotFound() {
        Long tripId = 1L;

        when(tripRepository.findById(tripId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tripService.updateStatus(tripId, TripStatus.COMPLETED))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Trip not found with ID: " + tripId);

        verify(tripRepository, times(1)).findById(tripId);
        verify(tripRepository, never()).save(any(TripEntity.class));
    }

    @Test
    void updateStatus_ShouldThrowException_WhenTripIsCancelled() {
        Long tripId = 1L;
        TripEntity trip = new TripEntity();
        trip.setId(tripId);
        trip.setStatus(TripStatus.CANCELLED);

        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

        assertThatThrownBy(() -> tripService.updateStatus(tripId, TripStatus.CREATED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot update status of a cancelled trip.");

        verify(tripRepository, times(1)).findById(tripId);
        verify(tripRepository, never()).save(any(TripEntity.class));
    }

    @Test
    void filterTrip_ShouldReturnTrips_WhenCriteriaMatch() {
        TripFilterDto filter = new TripFilterDto();
        filter.setDepartureLocationId(1L);
        filter.setDestinationLocationId(2L);
        filter.setSeats(3);
        filter.setStatus(TripStatus.CREATED);

        CityEntity departureCity = new CityEntity();
        CityEntity destinationCity = new CityEntity();

        TripEntity trip = new TripEntity();

        when(cityRepository.findById(1L)).thenReturn(Optional.of(departureCity));
        when(cityRepository.findById(2L)).thenReturn(Optional.of(destinationCity));

        verify(cityRepository, times(1)).findById(1L);
        verify(cityRepository, times(1)).findById(2L);
    }

    @Test
    void findPassengersByTripId_ShouldReturnPassengers_WhenTripIdExists() {
        Long tripId = 1L;
        PassengerEntity passenger = new PassengerEntity();

        when(passengerRepository.findAllByTripId(tripId)).thenReturn(List.of(passenger));

        List<PassengerEntity> result = tripService.findPassengersByTripId(tripId);

        assertThat(result).containsExactly(passenger);
        verify(passengerRepository, times(1)).findAllByTripId(tripId);
    }
}
