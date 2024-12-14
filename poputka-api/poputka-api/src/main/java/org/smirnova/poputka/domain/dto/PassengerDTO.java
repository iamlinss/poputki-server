package org.smirnova.poputka.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.smirnova.poputka.domain.enums.PassengerStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PassengerDTO {
    private Long id;
    private Long tripId;
    private UserSimpleDto user;
    private int seats;
    private PassengerStatus status;
    private Integer driverRating;
    private Integer passengerRating;
    private String driverComment;
    private String passengerComment;
}
