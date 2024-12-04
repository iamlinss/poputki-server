package org.smirnova.poputka.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.dto.trip.TripRqDto;
import org.smirnova.poputka.domain.dto.trip.TripFilterDto;
import org.smirnova.poputka.domain.dto.trip.TripRsDto;
import org.smirnova.poputka.domain.dto.CarDto;
import org.smirnova.poputka.domain.dto.trip.TripDto;
import org.smirnova.poputka.domain.dto.UserDto;
import org.smirnova.poputka.domain.entities.*;
import org.smirnova.poputka.domain.enums.PassengerStatus;
import org.smirnova.poputka.domain.enums.TripStatus;
import org.smirnova.poputka.mappers.Mapper;
import org.smirnova.poputka.repositories.CityRepository;
import org.smirnova.poputka.repositories.PassengerRepository;
import org.smirnova.poputka.repositories.TripRepository;
import org.smirnova.poputka.services.CarService;
import org.smirnova.poputka.services.TripService;
import org.smirnova.poputka.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final CityRepository cityRepository;
    private final UserService userService;
    private final Mapper<UserEntity, UserDto> userMapper;
    private final CarService carService;
    private final Mapper<CarEntity, CarDto> carMapper;
    private final PassengerRepository passengerRepository;
    private final EmailServiceImpl emailService;

    @Override
    public TripEntity save(TripEntity carEntity) {
        return tripRepository.save(carEntity);
    }

    @Override
    public void updateStatus(Long id, TripStatus newStatus) {
        TripEntity trip = tripRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found with ID: " + id));
        TripStatus oldStatus = trip.getStatus();

        if (oldStatus == TripStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot update status of a cancelled trip.");
        }

        trip.setStatus(newStatus);
        tripRepository.save(trip);

        // Отправка письма пользователю
        UserEntity user = trip.getUser();
        if (user != null && user.getEmail() != null) {
            String subject = "Status Update for Your Trip";
            String body = String.format(
                    "Dear %s %s,\n\nThe status of your trip has been updated from %s to %s.\n\nBest regards,\nYour Service Team",
                    user.getFirstName(), user.getLastName(), oldStatus.name(), newStatus.name()
            );

            emailService.sendMessage(user.getEmail(), subject, body);
        }
    }


    @Override
    public TripDto daoToDto(TripRqDto tripRqDto) {
        UserDto user = userToDto(tripRqDto.getUserId());
        return new TripDto(tripRqDto.getId(),
                findCity(tripRqDto.getDepartureLocationId()),
                findCity(tripRqDto.getDestinationLocationId()),
                tripRqDto.getDepartureDateTime(),
                tripRqDto.getDescription(),
                tripRqDto.getSeats(),
                user,
                carToDto(tripRqDto.getCarId()),
                user.getFirstName() + " " + user.getLastName(),
                tripRqDto.getPrice(),
                tripRqDto.getStatus()
        );
    }

    @Override
    public TripRsDto dtoToInfoDao(TripDto tripDto) {
        return new TripRsDto(tripDto.getId(),
                tripDto.getDepartureLocation(),
                tripDto.getDestinationLocation(),
                tripDto.getDepartureDateTime(),
                tripDto.getDescription(),
                tripDto.getSeats(),
                tripDto.getDriverName(),
                tripDto.getUser().getId(),
                tripDto.getCar(),
                tripDto.getPrice(),
                tripDto.getStatus());
    }

    @Override
    public List<TripEntity> filterTrip(TripFilterDto filter) {
        CityEntity departure = cityRepository.findById(filter.getDepartureLocationId()).orElse(null);
        CityEntity destination = cityRepository.findById(filter.getDestinationLocationId()).orElse(null);
        return tripRepository.findAllByFilter(departure, destination, filter.getSeats(), filter.getStatus());
    }

    private UserDto userToDto(Long id) {
        Optional<UserEntity> foundUser = userService.findOne(id);
        return foundUser.map(userMapper::mapTo).orElse(null);
    }

    private UserEntity userIdToEntity(Long id) {
        Optional<UserEntity> foundUser = userService.findOne(id);
        return foundUser.orElse(null);

    }

    private CarDto carToDto(Long id) {
        Optional<CarEntity> foundCar = carService.findOne(id);
        return foundCar.map(carMapper::mapTo).orElse(null);
    }

    private CityEntity findCity(Long id) {
        Optional<CityEntity> foundCity = cityRepository.findById(id);
        return foundCity.orElse(null);
    }

    @Override
    public List<TripEntity> findUserCreatedTrips(Long userId) {
        return tripRepository.findAllByUser(userIdToEntity(userId)).stream().toList();
    }

    public void updatePassengerStatus(Long id, PassengerStatus newStatus) {
        PassengerEntity passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Passenger not found with ID: " + id));
        PassengerStatus oldStatus = passenger.getStatus();

        passenger.setStatus(newStatus);
        passengerRepository.save(passenger);

        // Отправка письма пассажиру
        UserEntity user = userService.findOne(passenger.getUserId()).orElseThrow();
        if (user.getEmail() != null) {
            String subject = "Booking Status Update";
            String body = String.format(
                    "Dear %s %s,\n\nThe status of your booking has been updated from %s to %s.\n\nBest regards,\nYour Service Team",
                    user.getFirstName(), user.getLastName(), oldStatus.name(), newStatus.name()
            );

            emailService.sendMessage(user.getEmail(), subject, body);
        }
    }

    public List<PassengerEntity> findPassengersByTripId(Long tripId) {
        return passengerRepository.findAllByTripId(tripId);
    }
}
