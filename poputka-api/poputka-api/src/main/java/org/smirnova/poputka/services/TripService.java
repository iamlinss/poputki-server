package org.smirnova.poputka.services;

import org.smirnova.poputka.domain.dto.PassengerWithTripDto;
import org.smirnova.poputka.domain.dto.trip.TripRqDto;
import org.smirnova.poputka.domain.dto.trip.TripFilterDto;
import org.smirnova.poputka.domain.dto.trip.TripRsDto;
import org.smirnova.poputka.domain.dto.trip.TripDto;
import org.smirnova.poputka.domain.entities.PassengerEntity;
import org.smirnova.poputka.domain.entities.TripEntity;
import org.smirnova.poputka.domain.enums.PassengerStatus;
import org.smirnova.poputka.domain.enums.TripStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public interface TripService {
    List<TripEntity> findTripsByFilters(Long userId,
                                        LocalDate date,
                                        LocalTime startedAt,
                                        Long departureLocationId,
                                        Long destinationLocationId,
                                        TripStatus status,
                                        Integer seats);

    TripEntity save(TripEntity carEntity);

    Optional<TripEntity> findOne(Long id);

    void updateStatus(Long id, TripStatus status);

    TripDto daoToDto(TripRqDto tripRqDto);

    TripRsDto dtoToInfoDao(TripDto tripDto);

    List<TripEntity> filterTrip(TripFilterDto filter);

    List<TripEntity> findUserCreatedTrips(Long userId);

    void updatePassengerStatus(Long id, PassengerStatus status);

    List<PassengerEntity> findPassengersByTripId(Long tripId);

    List<PassengerEntity> findPassengersByTripIdAndStatus(Long tripId, PassengerStatus status);

    List<PassengerWithTripDto> getUserBronedTrips(Long userId);

    void sendBookingNotification(TripEntity tripEntity, PassengerEntity passengerEntity);

    TripRsDto convertToTripRsDto(TripEntity tripEntity);
}
