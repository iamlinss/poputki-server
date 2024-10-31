package org.bratanov.poputka.services.impl;

import lombok.RequiredArgsConstructor;
import org.bratanov.poputka.domain.dto.trip.TripRqDto;
import org.bratanov.poputka.domain.dto.trip.TripFilterDto;
import org.bratanov.poputka.domain.dto.trip.TripRsDto;
import org.bratanov.poputka.domain.dto.CarDto;
import org.bratanov.poputka.domain.dto.trip.TripDto;
import org.bratanov.poputka.domain.dto.UserDto;
import org.bratanov.poputka.domain.entities.*;
import org.bratanov.poputka.mappers.Mapper;
import org.bratanov.poputka.repositories.CityRepository;
import org.bratanov.poputka.repositories.StatusRepository;
import org.bratanov.poputka.repositories.TripRepository;
import org.bratanov.poputka.repositories.UserRepository;
import org.bratanov.poputka.services.CarService;
import org.bratanov.poputka.services.StatusService;
import org.bratanov.poputka.services.TripService;
import org.bratanov.poputka.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final CityRepository cityRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final Mapper<UserEntity, UserDto> userMapper;
    private final CarService carService;
    private final Mapper<CarEntity, CarDto> carMapper;
    private final StatusService statusService;
    private final StatusRepository statusRepository;

    @Override
    public TripEntity save(TripEntity carEntity) {
        return tripRepository.save(carEntity);
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
                statusService.getCreatedStatus(),
                user,
                carToDto(tripRqDto.getCarId()),
                user.getFirstName()+" "+user.getLastName(),
                tripRqDto.getPrice()
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
                        tripDto.getStatus(),
                        tripDto.getDriverName(),
                        tripDto.getUser().getId(),
                        tripDto.getCar(),
                tripDto.getPrice());
    }

    @Override
    public List<TripEntity> filterTrip(TripFilterDto filter) {
        CityEntity departure = cityRepository.findById(filter.getDepartureLocationId()).orElse(null);
        CityEntity destination = cityRepository.findById(filter.getDestinationLocationId()).orElse(null);
        StatusEntity status = statusRepository.findById(filter.getStatusId()).orElse(null);
        return StreamSupport.stream(tripRepository.findAllByFilter(departure,destination, filter.getSeats(), status).spliterator(),false).toList();
    }

    private UserDto userToDto(Long id) {
        Optional<UserEntity> foundUser = userService.findOne(id);
        return foundUser.map(userEntity -> {
            return userMapper.mapTo(userEntity);
        }).orElse(null);
    }

    private UserEntity userIdToEntity(Long id) {
        Optional<UserEntity> foundUser = userService.findOne(id);
        return foundUser.orElse(null);

    }

    private CarDto carToDto(Long id) {
        Optional<CarEntity> foundCar = carService.findOne(id);
        return foundCar.map(carEntity -> {
            return carMapper.mapTo(carEntity);
        }).orElse(null);
    }

    private CityEntity findCity(Long id) {
        Optional<CityEntity> foundCity = cityRepository.findById(id);
        return foundCity.orElse(null);
    }

    @Override
    public List<TripEntity> findUserCreatedTrips(Long userId) {
        return StreamSupport.stream(tripRepository.findAllByUser(userIdToEntity(userId)).spliterator(),false).toList();
    }
}
