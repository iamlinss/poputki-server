package org.smirnova.poputka.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.entities.PassengerEntity;
import org.smirnova.poputka.repositories.PassengerRepository;
import org.smirnova.poputka.services.ReviewService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final PassengerRepository passengerRepository;

    @Override
    public String leaveDriverReview(Long passengerId, Integer rating, String comment) {
        PassengerEntity passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new EntityNotFoundException("Passenger not found"));

        passenger.setDriverRating(rating);
        passenger.setDriverComment(comment);
        passengerRepository.save(passenger);

        return "Review for driver saved successfully.";
    }

    @Override
    public String leavePassengerReview(Long passengerId, Integer rating, String comment) {
        PassengerEntity passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new EntityNotFoundException("Passenger not found"));

        passenger.setPassengerRating(rating);
        passenger.setPassengerComment(comment);
        passengerRepository.save(passenger);

        return "Review for passenger saved successfully.";
    }
}
