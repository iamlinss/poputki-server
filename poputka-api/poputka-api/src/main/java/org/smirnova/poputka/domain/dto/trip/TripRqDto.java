package org.smirnova.poputka.domain.dto.trip;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.smirnova.poputka.domain.enums.TripStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripRqDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private Long departureLocationId;

    private Long destinationLocationId;

    private LocalDateTime departureDateTime;

    private String description;

    private int seats;

    private Long userId;

    private Long carId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String driverName;

    private int price;

    private TripStatus status = TripStatus.CREATED;
}
