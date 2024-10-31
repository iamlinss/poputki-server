package org.smirnova.poputka.auth.domain.dto;

public class AuthDTO {
    public record LoginRequest(String email, String password) {
    }

    public record Response(String token) {
    }
}
