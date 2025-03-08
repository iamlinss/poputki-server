package org.smirnova.poputka.services;

import org.smirnova.poputka.domain.entities.PassengerEntity;
import org.smirnova.poputka.domain.enums.PassengerStatus;

public interface PassengerService {
    PassengerEntity save(PassengerEntity passengerEntity);

    boolean existsByTripIdAndStatus(Long tripId, PassengerStatus status);
}
