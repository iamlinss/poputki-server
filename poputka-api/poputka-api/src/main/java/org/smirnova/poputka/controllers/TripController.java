package org.smirnova.poputka.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.dto.CarDto;
import org.smirnova.poputka.domain.dto.CityDto;
import org.smirnova.poputka.domain.dto.PassengerWithTripDto;
import org.smirnova.poputka.domain.dto.trip.*;
import org.smirnova.poputka.domain.entities.PassengerEntity;
import org.smirnova.poputka.domain.entities.TripEntity;
import org.smirnova.poputka.domain.entities.UserEntity;
import org.smirnova.poputka.domain.enums.PassengerStatus;
import org.smirnova.poputka.domain.enums.TripStatus;
import org.smirnova.poputka.mappers.Mapper;
import org.smirnova.poputka.mappers.impl.UserMapperImpl;
import org.smirnova.poputka.repositories.PassengerRepository;
import org.smirnova.poputka.repositories.TripRepository;
import org.smirnova.poputka.repositories.UserRepository;
import org.smirnova.poputka.services.TripService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smirnova.poputka.services.impl.EmailServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Trip endpoints", description = "Работа с поездками")
@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private static final Logger log = LoggerFactory.getLogger(TripController.class);

    private final Mapper<TripEntity, TripDto> tripMapper;
    private final TripService tripService;
    private final EmailServiceImpl emailService;
    private final UserRepository userRepository;
    private final UserMapperImpl userMapperImpl;
    private final TripRepository tripRepository;
    private final PassengerRepository passengerRepository;
    //TODO Перенести репозитории в сервисный слой

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
                            content = {@Content(schema = @Schema())}
                    ),
                    @ApiResponse(
                            description = "Не найден",
                            responseCode = "404",
                            content = {@Content(schema = @Schema())}
                    )
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(path = "/create")
    public ResponseEntity<TripRsDto> createTrip(@RequestBody TripRqDto trip) {
        log.info("CALL: Create trip: {}", trip);

        TripDto tripDto;
        try {
            tripDto = tripService.daoToDto(trip);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        tripDto.getUser().setTripAmount(tripDto.getUser().getTripAmount() + 1);
        TripEntity tripEntity = tripMapper.mapFrom(tripDto);

        TripEntity savedTripEntity = tripService.save(tripEntity);
        userRepository.save(userMapperImpl.mapFrom(tripDto.getUser()));

        return new ResponseEntity<>(tripService.dtoToInfoDao(tripMapper.mapTo(savedTripEntity)), HttpStatus.CREATED);
    }

    @Operation(
            description = "Изменение статуса поездки по ID",
            summary = "Изменить статус поездки",
            responses = {
                    @ApiResponse(description = "Успешно", responseCode = "200"),
                    @ApiResponse(description = "Не найдено", responseCode = "404"),
                    @ApiResponse(description = "Некорректный запрос", responseCode = "400")
            }
    )
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateTripStatus(@PathVariable Long id, @RequestParam TripStatus status) {
        log.info("CALL: Update trip status. ID: {}, Status: {}", id, status);
        try {
            tripService.updateStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(
            description = "Фильтрация поездок по параметрам, включая статус",
            summary = "Фильтрация поездок",
            responses = {
                    @ApiResponse(description = "Успешно", responseCode = "200"),
                    @ApiResponse(description = "Некорректный запрос", responseCode = "400")
            }
    )
    @PostMapping(path = "/filter")
    public ResponseEntity<List<TripRsDto>> filterTrip(@RequestBody TripFilterDto filter) {
        log.info("CALL: Filter trip: {}", filter);
        try {
            List<TripEntity> tripEntityList = tripService.filterTrip(filter);
            List<TripDto> tripDtoList = tripEntityList.stream()
                    .map(tripMapper::mapTo)
                    .toList();
            List<TripRsDto> tripRsDtoList = tripDtoList.stream()
                    .map(tripService::dtoToInfoDao)
                    .toList();
            return new ResponseEntity<>(tripRsDtoList, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error filtering trips", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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
                            content = {@Content(schema = @Schema())}
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
                            content = {@Content(schema = @Schema())}
                    ),
                    @ApiResponse(
                            description = "Не найден",
                            responseCode = "404",
                            content = {@Content(schema = @Schema())}
                    )
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(path = "/brone")
    public ResponseEntity<TripRsDto> broneTrip(@RequestBody PassengerEntity passengerEntity) {
        log.info("CALL: Brone trip: {}", passengerEntity);
        TripEntity tripEntity = tripRepository.findById(passengerEntity.getTripId()).orElse(null);
        if (tripEntity != null) {
            tripEntity.setSeats(tripEntity.getSeats() - passengerEntity.getSeats());
            TripEntity savedTripEntity = tripService.save(tripEntity);
            passengerRepository.save(passengerEntity);

            //TODO Вынести в сервисный слой
            // Получаем данные о пассажире
            UserEntity passenger = userRepository.findById(passengerEntity.getUserId()).orElse(null);
            if (passenger == null) {
                log.warn("Passenger with ID {} not found", passengerEntity.getUserId());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Отправляем уведомление водителю
            UserEntity driver = tripEntity.getUser();
            if (driver != null && driver.getEmail() != null) {
                String subject = "Новая бронь на вашу поездку";
                String body = String.format(
                        """
                                Здравствуйте, %s!

                                У вас новая бронь на поездку:

                                Отправление: %s
                                Назначение: %s
                                Дата и время: %s
                                Количество мест: %d

                                Детали о пассажире:
                                Имя: %s %s
                                Email: %s

                                Пожалуйста, проверьте детали в приложении.""",
                        driver.getFirstName(),
                        tripEntity.getDepartureLocation().getCity(),
                        tripEntity.getDestinationLocation().getCity(),
                        tripEntity.getDepartureDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        passengerEntity.getSeats(),
                        passenger.getFirstName(),
                        passenger.getLastName(),
                        passenger.getEmail()
                );

                emailService.sendMessage(driver.getEmail(), subject, body);
            }

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
                            content = {@Content(schema = @Schema())}
                    )
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(path = "brone/{id}")
    public ResponseEntity<List<PassengerWithTripDto>> getUserBronedTrips(@PathVariable Long id) {
        log.info("CALL: Get user broned trips by user ID {}", id);

        List<PassengerEntity> passengers = passengerRepository.findAllByUserId(id);

        List<PassengerWithTripDto> result = passengers.stream()
                .map(passenger -> {
                    TripEntity tripEntity = tripRepository.findById(passenger.getTripId())
                            .orElseThrow(() -> new EntityNotFoundException("Trip not found for ID: " + passenger.getTripId()));

                    // Преобразуем сущности городов в DTO
                    CityDto departureLocation = tripEntity.getDepartureLocation() != null
                            ? CityDto.builder()
                            .city(tripEntity.getDepartureLocation().getCity())
                            .country(tripEntity.getDepartureLocation().getCountry())
                            .build()
                            : null;

                    CityDto destinationLocation = tripEntity.getDestinationLocation() != null
                            ? CityDto.builder()
                            .city(tripEntity.getDestinationLocation().getCity())
                            .country(tripEntity.getDestinationLocation().getCountry())
                            .build()
                            : null;

                    // Преобразуем автомобиль в DTO
                    CarDto carDto = tripEntity.getCar() != null
                            ? CarDto.builder()
                            .brand(tripEntity.getCar().getBrand())
                            .model(tripEntity.getCar().getModel())
                            .color(tripEntity.getCar().getColor())
                            .plateNumber(tripEntity.getCar().getPlateNumber())
                            .maxSeats(tripEntity.getCar().getMaxSeats())
                            .build()
                            : null;

                    // Формируем TripDetails
                    TripDetails tripDetails = TripDetails.builder()
                            .departureDateTime(tripEntity.getDepartureDateTime())
                            .seats(tripEntity.getSeats())
                            .driverName(tripEntity.getDriverName())
                            .price(tripEntity.getPrice())
                            .status(tripEntity.getStatus())
                            .departureLocation(departureLocation)
                            .destinationLocation(destinationLocation)
                            .car(carDto)
                            .build();

                    return PassengerWithTripDto.builder()
                            .id(passenger.getId())
                            .tripDetails(tripDetails)
                            .passengerSeats(passenger.getSeats())
                            .passengerStatus(passenger.getStatus())
                            .build();
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(
            description = "Изменение статуса брони пассажира по ID",
            summary = "Изменить статус брони пассажира",
            responses = {
                    @ApiResponse(description = "Успешно", responseCode = "200"),
                    @ApiResponse(description = "Не найдено", responseCode = "404"),
                    @ApiResponse(description = "Некорректный запрос", responseCode = "400")
            }
    )
    @PutMapping("/{id}/passenger-status")
    public ResponseEntity<Void> updatePassengerStatus(@PathVariable Long id, @RequestParam PassengerStatus status) {
        log.info("CALL: Update passenger status. ID: {}, Status: {}", id, status);
        try {
            tripService.updatePassengerStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(
            description = "Получить все брони по tripId",
            summary = "Возвращает список пассажиров, забронировавших поездку по ID tripId",
            responses = {
                    @ApiResponse(description = "Успешно", responseCode = "200"),
                    @ApiResponse(description = "Не найдено", responseCode = "404")
            }
    )
    @GetMapping("/{tripId}/passengers")
    public ResponseEntity<List<PassengerEntity>> getPassengersByTripId(@PathVariable Long tripId) {
        log.info("CALL: Get passengers by trip ID: {}", tripId);
        List<PassengerEntity> passengers = tripService.findPassengersByTripId(tripId);
        if (passengers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(passengers);
    }
}
