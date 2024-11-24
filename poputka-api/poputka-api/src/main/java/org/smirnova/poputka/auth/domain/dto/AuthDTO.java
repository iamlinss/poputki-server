package org.smirnova.poputka.auth.domain.dto;

import org.smirnova.poputka.domain.dto.UserDto;

public class AuthDTO {

    public record LoginRequest(String email, String password) {
    }

    public record Response(String token, UserDto user) {
    }
}
