package org.smirnova.poputka.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.dto.*;
import org.smirnova.poputka.domain.dto.trip.*;
import org.smirnova.poputka.domain.entities.PassengerEntity;
import org.smirnova.poputka.domain.entities.TripEntity;
import org.smirnova.poputka.domain.enums.PassengerStatus;
import org.smirnova.poputka.domain.enums.TripStatus;
import org.smirnova.poputka.mappers.Mapper;
import org.smirnova.poputka.mappers.impl.UserMapperImpl;
import org.smirnova.poputka.services.PassengerService;
import org.smirnova.poputka.services.TripService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smirnova.poputka.services.UserService;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Trip endpoints", description = "Работа с поездками")
@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private static final Logger log = LoggerFactory.getLogger(TripController.class);

    private final Mapper<TripEntity, TripDto> tripMapper;
    private final UserService userService;
    private final TripService tripService;
    private final PassengerService passengerService;
    private final UserMapperImpl userMapperImpl;

    @Operation(
            summary = "Получение списка поездок с фильтрами",
            description = "Возвращает массив поездок с возможностью фильтрации по userId, дате, месту отправления, месту назначения, статусу, количеству оставшихся мест и начальному времени. " +
                    "Параметр date задаёт конкретную дату (формат: YYYY-MM-DD), " +
                    "startTime — время (формат: HH:mm), после которого должны начинаться поездки, независимо от даты. " +
                    "Если заданы параметры status и seats, возвращаются поездки с соответствующим статусом и количеством мест не меньше указанного.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список поездок успешно получен",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TripRsDto[].class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Не авторизован/ Токен не валидный",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<TripRsDto>> getTrips(
            @Parameter(description = "Идентификатор пользователя")
            @RequestParam(value = "userId", required = false) Long userId,

            @Parameter(description = "Дата поездки (формат: YYYY-MM-DD)", example = "2025-02-22")
            @RequestParam(value = "date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,

            @Parameter(description = "Идентификатор места отправления")
            @RequestParam(value = "departureLocation", required = false) Long departureLocationId,

            @Parameter(description = "Идентификатор места назначения")
            @RequestParam(value = "destinationLocation", required = false) Long destinationLocationId,

            @Parameter(description = "Статус поездки", example = "CREATED")
            @RequestParam(value = "status", required = false) TripStatus status,

            @Parameter(description = "Минимальное количество оставшихся мест", example = "2")
            @RequestParam(value = "seats", required = false) Integer seats,

            @Parameter(description = "Начальное время поездки (формат: HH:mm)", example = "09:00")
            @RequestParam(value = "startTime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime) {
        try {
            List<TripEntity> trips = tripService.findTripsByFilters(userId, date, departureLocationId, destinationLocationId, status, seats, startTime);
            List<TripRsDto> tripsDto = trips.stream()
                    .map(tripMapper::mapTo)
                    .map(tripService::dtoToInfoDao)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(tripsDto, HttpStatus.OK);
        } catch (DataAccessException ex) {
            log.error("Ошибка при выполнении запроса с фильтрами", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
        userService.save(userMapperImpl.mapFrom(tripDto.getUser()));

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
    public ResponseEntity<List<TripWithPendingStatusDto>> getUserTrips(@PathVariable Long id) {
        log.info("CALL: Get user trips by user ID {}", id);

        List<TripEntity> tripEntityList = tripService.findUserCreatedTrips(id);

        List<TripWithPendingStatusDto> tripWithPendingStatusDtoList = tripEntityList.stream()
                .map(tripEntity -> {
                    // Проверяем, есть ли у этой поездки пассажиры с статусом PENDING
                    boolean hasPendingPassengers = passengerService.existsByTripIdAndStatus(tripEntity.getId(), PassengerStatus.PENDING_CONFIRMATION);

                    TripRsDto tripRsDto = tripService.dtoToInfoDao(tripMapper.mapTo(tripEntity));

                    return TripWithPendingStatusDto.builder()
                            .id(tripRsDto.getId())
                            .departureLocation(tripRsDto.getDepartureLocation())
                            .destinationLocation(tripRsDto.getDestinationLocation())
                            .departureDateTime(tripRsDto.getDepartureDateTime())
                            .description(tripRsDto.getDescription())
                            .seats(tripRsDto.getSeats())
                            .driverName(tripRsDto.getDriverName())
                            .userId(tripRsDto.getUserId())
                            .car(tripRsDto.getCar())
                            .price(tripRsDto.getPrice())
                            .status(tripRsDto.getStatus())
                            .hasPendingPassengers(hasPendingPassengers)
                            .build();
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(tripWithPendingStatusDtoList, HttpStatus.OK);
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

        TripEntity tripEntity = tripService.findOne(passengerEntity.getTripId())
                .orElseThrow(() -> new EntityNotFoundException("Trip not found"));

        // Обновляем количество мест
        tripEntity.setSeats(tripEntity.getSeats() - passengerEntity.getSeats());
        TripEntity savedTripEntity = tripService.save(tripEntity);

        passengerService.save(passengerEntity);

        // Отправляем уведомление водителю
        tripService.sendBookingNotification(tripEntity, passengerEntity);

        TripRsDto tripRsDto = tripService.convertToTripRsDto(savedTripEntity);

        return new ResponseEntity<>(tripRsDto, HttpStatus.CREATED);
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

        List<PassengerWithTripDto> result = tripService.getUserBronedTrips(id);

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
            description = "Получить все брони по tripId с фильтром по статусу",
            summary = "Возвращает список пассажиров, забронировавших поездку по ID tripId с возможностью фильтрации по статусу",
            responses = {
                    @ApiResponse(description = "Успешно", responseCode = "200"),
                    @ApiResponse(description = "Не найдено", responseCode = "404")
            }
    )
    @GetMapping("/{tripId}/passengers")
    public ResponseEntity<List<PassengerDTO>> getPassengersByTripId(
            @PathVariable Long tripId,
            @RequestParam(required = false) PassengerStatus status) {
        log.info("CALL: Get passengers by trip ID: {} with status: {}", tripId, status);

        List<PassengerEntity> passengers = tripService.findPassengersByTripIdAndStatus(tripId, status);

        List<PassengerDTO> passengerDTOs = passengers.stream().map(passenger -> {
            UserSimpleDto userSimpleDto = userService.getUserSimpleDtoById(passenger.getUserId());
            return PassengerDTO.builder()
                    .id(passenger.getId())
                    .tripId(passenger.getTripId())
                    .user(userSimpleDto)
                    .seats(passenger.getSeats())
                    .status(passenger.getStatus())
                    .driverRating(passenger.getDriverRating())
                    .passengerRating(passenger.getPassengerRating())
                    .driverComment(passenger.getDriverComment())
                    .passengerComment(passenger.getPassengerComment())
                    .build();
        }).collect(Collectors.toList());

        return ResponseEntity.ok(passengerDTOs);
    }
}
