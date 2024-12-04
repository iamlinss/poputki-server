package org.smirnova.poputka.domain.dto;

import lombok.Data;

@Data
public class PassengerFeedbackDto {
    private Integer rating;
    private String comment;
}
