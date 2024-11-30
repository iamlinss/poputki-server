package org.smirnova.poputka.services;

import jakarta.persistence.EntityNotFoundException;
import org.smirnova.poputka.domain.dto.trip.TripRqDto;
import org.smirnova.poputka.domain.dto.trip.TripFilterDto;
import org.smirnova.poputka.domain.dto.trip.TripRsDto;
import org.smirnova.poputka.domain.dto.trip.TripDto;
import org.smirnova.poputka.domain.entities.PassengerEntity;
import org.smirnova.poputka.domain.entities.TripEntity;
import org.smirnova.poputka.domain.enums.PassengerStatus;
import org.smirnova.poputka.domain.enums.TripStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TripService {

    TripEntity save(TripEntity carEntity);

    void updateStatus(Long id, TripStatus status);

    TripDto daoToDto(TripRqDto tripRqDto);

    TripRsDto dtoToInfoDao(TripDto tripDto);

    List<TripEntity> filterTrip(TripFilterDto filter);

    List<TripEntity> findUserCreatedTrips(Long userId);

    void updatePassengerStatus(Long id, PassengerStatus status);

    List<PassengerEntity> findPassengersByTripId(Long tripId);
}
