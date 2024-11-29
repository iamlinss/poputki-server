package org.smirnova.poputka.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.smirnova.poputka.domain.enums.UserRole;

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

    private UserRole role;
}
