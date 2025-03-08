package org.smirnova.poputka.services.impl;

import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.entities.PassengerEntity;
import org.smirnova.poputka.domain.enums.PassengerStatus;
import org.smirnova.poputka.repositories.PassengerRepository;
import org.smirnova.poputka.services.PassengerService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;

    @Override
    public PassengerEntity save(PassengerEntity passengerEntity) {
        return passengerRepository.save(passengerEntity);
    }

    @Override
    public boolean existsByTripIdAndStatus(Long tripId, PassengerStatus status) {
        return passengerRepository.existsByTripIdAndStatus(tripId, status);
    }
}
