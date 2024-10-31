package org.smirnova.poputka.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String brand;

    private String model;

    private String color;

    private String plateNumber;

}
