package org.smirnova.poputka.services.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.dto.CityDto;
import org.smirnova.poputka.domain.dto.PassengerWithTripDto;
import org.smirnova.poputka.domain.dto.trip.*;
import org.smirnova.poputka.domain.dto.CarDto;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    //TODO Разделить на сервис для поездок (TripEntity) и сервис для броней (PassengerEntity)

    private final TripRepository tripRepository;
    private final CityRepository cityRepository;
    private final UserService userService;
    private final Mapper<UserEntity, UserDto> userMapper;
    private final CarService carService;
    private final Mapper<CarEntity, CarDto> carMapper;
    private final PassengerRepository passengerRepository;
    private final EmailServiceImpl emailService;
    private final EntityManager entityManager;

    @Override
    public List<TripEntity> findTripsByFilters(Long userId,
                                               LocalDate date,
                                               Long departureLocationId,
                                               Long destinationLocationId,
                                               TripStatus status,
                                               Integer seats,
                                               LocalTime startTime) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TripEntity> cq = cb.createQuery(TripEntity.class);
        Root<TripEntity> trip = cq.from(TripEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        // Фильтр по userId
        if (userId != null) {
            predicates.add(cb.equal(trip.get("user").get("id"), userId));
        }

        // Фильтр по дате и времени
        if (date != null) {
            // Извлекаем только дату из departureDateTime
            predicates.add(cb.equal(cb.function("DATE", LocalDate.class, trip.get("departureDateTime")), date));
        }

        // Фильтр по месту отправления
        if (departureLocationId != null) {
            predicates.add(cb.equal(trip.get("departureLocation").get("id"), departureLocationId));
        }

        // Фильтр по месту назначения
        if (destinationLocationId != null) {
            predicates.add(cb.equal(trip.get("destinationLocation").get("id"), destinationLocationId));
        }

        // Фильтр по статусу поездки
        if (status != null) {
            predicates.add(cb.equal(trip.get("status"), status));
        }

        // Фильтр по количеству оставшихся мест (если, например, пользователь хочет видеть поездки, где доступно не менее N мест)
        if (seats != null) {
            predicates.add(cb.greaterThanOrEqualTo(trip.get("seats"), seats));
        }

        // Фильтр по начальному времени (независимо от даты)
        if (startTime != null) {
            Expression<String> timeAsString = cb.function("to_char", String.class, trip.get("departureDateTime"), cb.literal("HH24:MI"));
            predicates.add(cb.greaterThanOrEqualTo(timeAsString, startTime.format(DateTimeFormatter.ofPattern("HH:mm"))));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public TripEntity save(TripEntity tripEntity) {
        return tripRepository.save(tripEntity);
    }

    @Override
    public Optional<TripEntity> findOne(Long id) {
        return tripRepository.findById(id);
    }

    @Override
    public void updateStatus(Long id, TripStatus newStatus) {
        TripEntity trip = findTripById(id);
        TripStatus oldStatus = trip.getStatus();

        if (oldStatus == TripStatus.CANCELLED) {
            throw new IllegalArgumentException("Невозможно обновить статус отменённой поездки.");
        }

        // Если статус меняется на отменённый, обновляем статус всех связанных пассажиров
        if (newStatus == TripStatus.CANCELLED) {
            List<PassengerEntity> passengers = passengerRepository.findByTripId(id);
            for (PassengerEntity passenger : passengers) {
                updatePassengerStatus(passenger.getId(), PassengerStatus.REJECTED_BY_DRIVER);
            }
        }

        trip.setStatus(newStatus);
        tripRepository.save(trip);

        sendStatusUpdateEmail(trip.getUser(), oldStatus, newStatus);
    }

    @Override
    public void updatePassengerStatus(Long id, PassengerStatus newStatus) {
        PassengerEntity passenger = findPassengerById(id);
        PassengerStatus oldStatus = passenger.getStatus();

        passenger.setStatus(newStatus);
        passengerRepository.save(passenger);

        UserEntity user = userService.findOne(passenger.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден."));
        sendStatusUpdateEmail(user, oldStatus, newStatus);
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
                calculateDriverRating(tripDto.getUser().getId()),
                tripDto.getUser().getId(),
                tripDto.getCar(),
                tripDto.getPrice(),
                tripDto.getStatus());
    }

    public double calculateDriverRating(Long driverUserId) {
        List<PassengerEntity> passengerReviews = passengerRepository.findAllByTripUser(driverUserId);

        return passengerReviews.stream()
                .filter(review -> review.getDriverRating() != null)
                .mapToInt(PassengerEntity::getDriverRating)
                .average()
                .orElse(0.0);
    }

    @Override
    public List<TripEntity> filterTrip(TripFilterDto filter) {
        CityEntity departure = findCity(filter.getDepartureLocationId());
        CityEntity destination = findCity(filter.getDestinationLocationId());
        return tripRepository.findAllByFilter(departure, destination, filter.getSeats(), filter.getStatus());
    }

    @Override
    public List<TripEntity> findUserCreatedTrips(Long userId) {
        return tripRepository.findAllByUser(userIdToEntity(userId));
    }

    @Override
    public List<PassengerEntity> findPassengersByTripId(Long tripId) {
        return passengerRepository.findAllByTripId(tripId);
    }

    @Override
    public List<PassengerEntity> findPassengersByTripIdAndStatus(Long tripId, PassengerStatus status) {
        if (status == null) {
            return passengerRepository.findByTripId(tripId);
        } else {
            return passengerRepository.findByTripIdAndStatus(tripId, status);
        }
    }

    // Private helper methods
    private TripEntity findTripById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found with ID: " + id));
    }

    private PassengerEntity findPassengerById(Long id) {
        return passengerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Passenger not found with ID: " + id));
    }

    private CityEntity findCity(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("City not found with ID: " + id));
    }

    private UserDto userToDto(Long id) {
        return userService.findOne(id)
                .map(userMapper::mapTo)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    private UserEntity userIdToEntity(Long id) {
        return userService.findOne(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    private CarDto carToDto(Long id) {
        return carService.findOne(id)
                .map(carMapper::mapTo)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with ID: " + id));
    }

    private void sendStatusUpdateEmail(UserEntity user, Enum<?> oldStatus, Enum<?> newStatus) {
        if (user != null && user.getEmail() != null) {
            String subject = "Обновление статуса";
            String body = String.format(
                    "Уважаемый(ая) %s %s,\n\nСтатус вашего объекта был изменён с \"%s\" на \"%s\".\n\nС уважением,\nКоманда сервиса",
                    user.getFirstName(), user.getLastName(), oldStatus.toString(), newStatus.toString()
            );

            emailService.sendMessage(user.getEmail(), subject, body);
        }
    }

    @Override
    public List<PassengerWithTripDto> getUserBronedTrips(Long userId) {
        List<PassengerEntity> passengers = passengerRepository.findAllByUserId(userId);

        return passengers.stream()
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
                            .driverId(tripEntity.getUser().getId())
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
    }


    public TripRsDto bookTrip(PassengerEntity passengerEntity) {
        // Получаем поездку по ID
        TripEntity tripEntity = tripRepository.findById(passengerEntity.getTripId())
                .orElseThrow(() -> new EntityNotFoundException("Trip not found"));

        // Обновляем количество мест
        tripEntity.setSeats(tripEntity.getSeats() - passengerEntity.getSeats());
        TripEntity savedTripEntity = tripRepository.save(tripEntity);

        // Сохраняем информацию о пассажире
        passengerRepository.save(passengerEntity);

        // Отправляем уведомление водителю
        sendBookingNotification(tripEntity, passengerEntity);

        // Преобразуем TripEntity в TripRsDto
        return convertToTripRsDto(savedTripEntity);
    }

    public void sendBookingNotification(TripEntity tripEntity, PassengerEntity passengerEntity) {
        // Получаем данные о пассажире
        UserEntity userEntity = userService.findOne(passengerEntity.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Passenger not found"));

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
                    userEntity.getFirstName(),
                    userEntity.getLastName(),
                    userEntity.getEmail()
            );

            emailService.sendMessage(driver.getEmail(), subject, body);
        }
    }

    // Преобразование TripEntity в TripRsDto
    @Override
    public TripRsDto convertToTripRsDto(TripEntity tripEntity) {
        CarDto carDto = convertCarEntityToCarDto(tripEntity.getCar());

        return TripRsDto.builder()
                .id(tripEntity.getId())
                .departureLocation(tripEntity.getDepartureLocation())
                .destinationLocation(tripEntity.getDestinationLocation())
                .departureDateTime(tripEntity.getDepartureDateTime())
                .description(tripEntity.getDescription())
                .seats(tripEntity.getSeats())
                .car(carDto)
                .driverName(tripEntity.getDriverName())
                .price(tripEntity.getPrice())
                .status(tripEntity.getStatus())
                .build();
    }

    // Метод для преобразования CarEntity в CarDto
    private CarDto convertCarEntityToCarDto(CarEntity carEntity) {
        if (carEntity == null) {
            return null;
        }

        return CarDto.builder()
                .id(carEntity.getId())
                .brand(carEntity.getBrand())
                .model(carEntity.getModel())
                .color(carEntity.getColor())
                .plateNumber(carEntity.getPlateNumber())
                .maxSeats(carEntity.getMaxSeats())
                .build();
    }
}
