package org.bratanov.poputka.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEditDto {

    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    private String description;

    private String phone;
}
