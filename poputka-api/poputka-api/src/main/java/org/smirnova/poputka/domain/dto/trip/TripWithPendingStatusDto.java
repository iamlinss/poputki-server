package org.smirnova.poputka.domain.dto.trip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.smirnova.poputka.domain.dto.CarDto;
import org.smirnova.poputka.domain.entities.CityEntity;
import org.smirnova.poputka.domain.enums.TripStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripWithPendingStatusDto {

    private Long id;
    private CityEntity departureLocation;
    private CityEntity destinationLocation;
    private LocalDateTime departureDateTime;
    private String description;
    private int seats;
    private String driverName;
    private Long userId;
    private CarDto car;
    private int price;
    private TripStatus status;
    private boolean hasPendingPassengers;
}
