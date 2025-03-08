package org.smirnova.poputka.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.smirnova.poputka.domain.enums.UserRole;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private LocalDate birthDate;

    private char gender;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String description;

    private String phone;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private double rate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate registrationDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int tripAmount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<CarDto> cars;

    private UserRole role;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<ReviewDto> reviews;
}
