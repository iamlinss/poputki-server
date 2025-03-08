package org.smirnova.poputka.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.smirnova.poputka.domain.dto.trip.TripDetails;
import org.smirnova.poputka.domain.enums.PassengerStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PassengerWithTripDto {
    private Long id;
    private TripDetails tripDetails;
    private int passengerSeats;
    private PassengerStatus passengerStatus;
}
