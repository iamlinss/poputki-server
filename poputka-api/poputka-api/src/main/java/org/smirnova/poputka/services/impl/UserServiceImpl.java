package org.smirnova.poputka.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.dto.ReviewDto;
import org.smirnova.poputka.domain.dto.UserDto;
import org.smirnova.poputka.domain.dto.UserSimpleDto;
import org.smirnova.poputka.domain.entities.PassengerEntity;
import org.smirnova.poputka.domain.entities.UserEntity;
import org.smirnova.poputka.domain.enums.UserRole;
import org.smirnova.poputka.mappers.Mapper;
import org.smirnova.poputka.repositories.PassengerRepository;
import org.smirnova.poputka.repositories.UserRepository;
import org.smirnova.poputka.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    //TODO Разделить логику по разным сервисам
    private final PassengerRepository passengerRepository;
    private final Mapper<UserEntity, UserDto> userMapper;

    @Override
    public UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    @Override
    public List<UserEntity> findAll() {
        return userRepository.findAll().stream().toList();
    }

    @Override
    public Optional<UserEntity> findOne(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean isExists(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public Optional<UserDto> getUserWithRatingsAndComments(Long id) {
        Optional<UserEntity> userEntityOpt = userRepository.findById(id);
        if (userEntityOpt.isEmpty()) {
            return Optional.empty();
        }

        UserEntity userEntity = userEntityOpt.get();
        UserDto userDto = userMapper.mapTo(userEntity);

        if (userEntity.getRole() == UserRole.DRIVER) {
            populateDriverData(userEntity, userDto);
        } else if (userEntity.getRole() == UserRole.USER) {
            populatePassengerData(userEntity, userDto);
        }

        return Optional.of(userDto);
    }

    private void populateDriverData(UserEntity driver, UserDto userDto) {
        List<PassengerEntity> passengerReviews = passengerRepository.findAllByTripUser(driver.getId());

        double driverRating = passengerReviews.stream()
                .filter(review -> review.getDriverRating() != null)
                .mapToInt(PassengerEntity::getDriverRating)
                .average()
                .orElse(0.0);
        userDto.setRate(driverRating);

        List<ReviewDto> driverReviews = passengerReviews.stream()
                .filter(review -> review.getDriverComment() != null)
                .map(review -> ReviewDto.builder()
                        .rating(review.getDriverRating())
                        .comment(review.getDriverComment())
                        .build())
                .toList();
        userDto.setReviews(driverReviews);
    }

    private void populatePassengerData(UserEntity passenger, UserDto userDto) {
        List<PassengerEntity> userTrips = passengerRepository.findAllByUserId(passenger.getId());

        double passengerRating = userTrips.stream()
                .filter(review -> review.getPassengerRating() != null)
                .mapToInt(PassengerEntity::getPassengerRating)
                .average()
                .orElse(0.0);
        userDto.setRate(passengerRating);

        List<ReviewDto> passengerReviews = userTrips.stream()
                .filter(review -> review.getPassengerComment() != null)
                .map(review -> ReviewDto.builder()
                        .rating(review.getPassengerRating())
                        .comment(review.getPassengerComment())
                        .build())
                .toList();
        userDto.setReviews(passengerReviews);
    }

    @Override
    public UserSimpleDto getUserSimpleDtoById(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return convertToSimpleDto(userEntity);
    }


    private UserSimpleDto convertToSimpleDto(UserEntity userEntity) {
        return UserSimpleDto.builder()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .rate(userEntity.getRate())
                .build();
    }
}
