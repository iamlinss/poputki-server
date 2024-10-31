package org.smirnova.poputka.services;

import org.smirnova.poputka.domain.dto.trip.TripRqDto;
import org.smirnova.poputka.domain.dto.trip.TripFilterDto;
import org.smirnova.poputka.domain.dto.trip.TripRsDto;
import org.smirnova.poputka.domain.dto.trip.TripDto;
import org.smirnova.poputka.domain.entities.TripEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TripService {

    TripEntity save(TripEntity carEntity);

    TripDto daoToDto(TripRqDto tripRqDto);

    TripRsDto dtoToInfoDao(TripDto tripDto);

    List<TripEntity> filterTrip(TripFilterDto filter);

    List<TripEntity> findUserCreatedTrips(Long userId);
}
