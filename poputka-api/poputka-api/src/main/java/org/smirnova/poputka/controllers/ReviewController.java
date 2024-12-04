package org.smirnova.poputka.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.entities.PassengerEntity;
import org.smirnova.poputka.repositories.PassengerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;

@Tag(name = "Reviews", description = "API для управления отзывами между пассажирами и водителями")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final PassengerRepository passengerRepository;

    @Operation(
            summary = "Оставить отзыв водителю",
            description = "Позволяет пользователю оставить отзыв водителю, включая рейтинг и комментарий."
    )
    @PostMapping("/to-driver/{passengerId}")
    public ResponseEntity<String> leaveDriverReview(
            @Parameter(description = "Идентификатор записи пассажира", example = "1")
            @PathVariable Long passengerId,

            @Parameter(description = "Рейтинг водителя от пользователя (1-5)", example = "5")
            @RequestParam(required = false) Integer rating,

            @Parameter(description = "Комментарий для водителя", example = "Отличный водитель, приехал вовремя.")
            @RequestParam(required = false) String comment
    ) {
        PassengerEntity passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new EntityNotFoundException("Passenger not found"));

        passenger.setDriverRating(rating);
        passenger.setDriverComment(comment);
        passengerRepository.save(passenger);

        return ResponseEntity.ok("Review for driver saved successfully.");
    }

    @Operation(
            summary = "Оставить отзыв пассажиру",
            description = "Позволяет водителю оставить отзыв пассажиру, включая рейтинг и комментарий."
    )
    @PostMapping("/to-passenger/{passengerId}")
    public ResponseEntity<String> leavePassengerReview(
            @Parameter(description = "Идентификатор записи пассажира", example = "1")
            @PathVariable Long passengerId,

            @Parameter(description = "Рейтинг пассажира от водителя (1-5)", example = "4")
            @RequestParam(required = false) Integer rating,

            @Parameter(description = "Комментарий для пассажира", example = "Пассажир опоздал, но был вежлив.")
            @RequestParam(required = false) String comment
    ) {
        PassengerEntity passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new EntityNotFoundException("Passenger not found"));

        passenger.setPassengerRating(rating);
        passenger.setPassengerComment(comment);
        passengerRepository.save(passenger);

        return ResponseEntity.ok("Review for passenger saved successfully.");
    }
}
