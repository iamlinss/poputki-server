package org.bratanov.poputka.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bratanov.poputka.domain.dto.trip.TripRqDto;
import org.bratanov.poputka.domain.dto.trip.TripFilterDto;
import org.bratanov.poputka.domain.dto.trip.TripRsDto;
import org.bratanov.poputka.domain.dto.trip.TripDto;
import org.bratanov.poputka.domain.entities.PassengerEntity;
import org.bratanov.poputka.domain.entities.TripEntity;
import org.bratanov.poputka.mappers.Mapper;
import org.bratanov.poputka.mappers.impl.UserMapperImpl;
import org.bratanov.poputka.repositories.PassengerRepositiry;
import org.bratanov.poputka.repositories.TripRepository;
import org.bratanov.poputka.repositories.UserRepository;
import org.bratanov.poputka.services.TripService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Trip endpoints", description = "Работа с поездками")
@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private static final Logger log = LoggerFactory.getLogger(TripController.class);

    private final Mapper<TripEntity, TripDto> tripMapper;
    private final TripService tripService;
    private final UserRepository userRepository;
    private final UserMapperImpl userMapperImpl;
    private final TripRepository tripRepository;
    private final PassengerRepositiry passengerRepositiry;

    @Operation(
            description = "Создание поездки (id не нужно передавать, так на всякий оставил)",
            summary = "Создание поездки",
            responses = {
                    @ApiResponse(
                            description = "Создана",
                            responseCode = "201",
                            content = @Content(schema = @Schema(implementation = TripRqDto.class), mediaType = "application/json")
                    ),
                    @ApiResponse(
                            description = "Не авторизован/ Токен не валидный",
                            responseCode = "401",
                            content = { @Content(schema = @Schema()) }
                    ),
                    @ApiResponse(
                            description = "Не найден",
                            responseCode = "404",
                            content = { @Content(schema = @Schema()) }
                    )
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(path = "/create")
    public ResponseEntity<TripRsDto> createTrip(@RequestBody TripRqDto trip) {
        log.info("CALL: Create trip: {}", trip);

        TripDto tripDto = new TripDto();
        try {
            tripDto = tripService.daoToDto(trip);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        tripDto.getUser().setTripAmount(tripDto.getUser().getTripAmount()+1);
        TripEntity tripEntity = tripMapper.mapFrom(tripDto);

        TripEntity savedTripEntity = tripService.save(tripEntity);
        userRepository.save(userMapperImpl.mapFrom(tripDto.getUser()));

        return new ResponseEntity<>(tripService.dtoToInfoDao(tripMapper.mapTo(savedTripEntity)), HttpStatus.CREATED);
    }

    @Operation(
            description = "Может редактироваться (по запросу). Если для фильтра поле отсутствует, то не передавать его! Поля-id значений (кроме кол-ва мест)",
            summary = "Фильтрация(поиск) поездок",
            responses = {
                    @ApiResponse(
                            description = "Успешно",
                            responseCode = "200"
                    )
            }
    )
    @PostMapping(path = "/filter")
    public ResponseEntity<List<TripRsDto>> filterTrip(@RequestBody TripFilterDto filter) {
        log.info("CALL: Filter trip: {}", filter);

        List<TripEntity> tripEntityList = tripService.filterTrip(filter);
        List<TripDto> tripDtoList = tripEntityList.stream()
                .map(tripMapper::mapTo)
                .toList();
        List<TripRsDto> tripRsDtoList = tripDtoList.stream()
                .map(tripService::dtoToInfoDao)
                .toList();
        return new ResponseEntity<>(tripRsDtoList, HttpStatus.OK);
    }

    @Operation(
            description = "Получить созданные поездки пользователя по ID",
            summary = "Поездки по ID пользователя (созданные)",
            responses = {
                    @ApiResponse(
                            description = "Успешно",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Не найден",
                            responseCode = "404",
                            content = { @Content(schema = @Schema()) }
                    )
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(path = "/{id}")
    public ResponseEntity<List<TripRsDto>> getUserTrips(@PathVariable Long id) {
        log.info("CALL: Get user trips by user ID {}", id);

        List<TripEntity> tripEntityList = tripService.findUserCreatedTrips(id);
        List<TripDto> tripDtoList = tripEntityList.stream()
                .map(tripMapper::mapTo)
                .toList();
        List<TripRsDto> tripRsDtoList = tripDtoList.stream()
                .map(tripService::dtoToInfoDao)
                .toList();
        return new ResponseEntity<>(tripRsDtoList, HttpStatus.OK);
    }

    @Operation(
            description = "Бронирование поездки, все поля обязательны и желательно корректны)",
            summary = "Бронирование поездки",
            responses = {
                    @ApiResponse(
                            description = "Забронирована",
                            responseCode = "201",
                            content = @Content(schema = @Schema(implementation = TripRqDto.class), mediaType = "application/json")
                    ),
                    @ApiResponse(
                            description = "Не авторизован/ Токен не валидный",
                            responseCode = "401",
                            content = { @Content(schema = @Schema()) }
                    ),
                    @ApiResponse(
                            description = "Не найден",
                            responseCode = "404",
                            content = { @Content(schema = @Schema()) }
                    )
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(path = "/brone")
    public ResponseEntity<TripRsDto> broneTrip(@RequestBody PassengerEntity passengerEntity) {
        log.info("CALL: Brone trip: {}", passengerEntity);
        TripEntity tripEntity = tripRepository.findById(passengerEntity.getTripId()).orElse(null);
        if(tripEntity != null){
            tripEntity.setSeats(tripEntity.getSeats() - passengerEntity.getSeats());
            TripEntity savedTripEntity = tripService.save(tripEntity);
            passengerRepositiry.save(passengerEntity);
            return new ResponseEntity<>(tripService.dtoToInfoDao(tripMapper.mapTo(savedTripEntity)), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @Operation(
            description = "Получить забронированные поездки пользователя по ID",
            summary = "Поездки по ID пользователя (забронированные)",
            responses = {
                    @ApiResponse(
                            description = "Успешно",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Не найден",
                            responseCode = "404",
                            content = { @Content(schema = @Schema()) }
                    )
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(path = "brone/{id}")
    public ResponseEntity<List<TripRsDto>> getUserBronedTrips(@PathVariable Long id) {
        log.info("CALL: Get user broned trips by user ID {}", id);

        List<PassengerEntity> passengers = passengerRepositiry.findAllByUserId(id);
        List<TripEntity> tripEntityList = new ArrayList<>();
        for (PassengerEntity passenger : passengers) {
            TripEntity trip = tripRepository.findById(passenger.getTripId()).orElse(null);
            if(trip != null) {
                trip.setSeats(passenger.getSeats());
                tripEntityList.add(trip);
            }
        }
        List<TripDto> tripDtoList = tripEntityList.stream()
                .map(tripMapper::mapTo)
                .toList();
        List<TripRsDto> tripRsDtoList = tripDtoList.stream()
                .map(tripService::dtoToInfoDao)
                .toList();
        return new ResponseEntity<>(tripRsDtoList, HttpStatus.OK);
    }
}
