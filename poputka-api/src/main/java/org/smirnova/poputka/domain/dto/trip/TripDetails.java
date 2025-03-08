package org.smirnova.poputka.domain.dto.trip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.smirnova.poputka.domain.dto.CarDto;
import org.smirnova.poputka.domain.dto.CityDto;
import org.smirnova.poputka.domain.enums.TripStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripDetails {
    private LocalDateTime departureDateTime;
    private int seats;
    private String driverName;
    private int price;
    private TripStatus status;

    private CityDto departureLocation;
    private CityDto destinationLocation;

    private CarDto car;
}
