package org.smirnova.poputka.domain.dto.trip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.smirnova.poputka.domain.enums.TripStatus;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripFilterDto {

    private Long departureLocationId = 0L;

    private Long destinationLocationId = 0L;

    private int seats = 0;

    private Long statusId = 0L;

    private TripStatus status = TripStatus.CREATED;
}
