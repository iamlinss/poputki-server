package org.smirnova.poputka.domain.dto.trip;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.smirnova.poputka.domain.dto.CarDto;
import org.smirnova.poputka.domain.dto.UserDto;
import org.smirnova.poputka.domain.entities.CityEntity;
import org.smirnova.poputka.domain.enums.TripStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private CityEntity departureLocation;

    private CityEntity destinationLocation;

    private LocalDateTime departureDateTime;

    private String description;

    private int seats;

    private UserDto user;

    private CarDto car;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String driverName;

    private int price;

    private TripStatus status = TripStatus.CREATED;
}
