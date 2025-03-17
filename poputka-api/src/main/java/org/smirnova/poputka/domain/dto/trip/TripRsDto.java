package org.smirnova.poputka.domain.dto.trip;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class TripRsDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CityEntity departureLocation;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CityEntity destinationLocation;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime departureDateTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int seats;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String driverName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double rate;

    private Long userId;

    private CarDto car;

    private int price;

    private TripStatus status = TripStatus.CREATED;
}
