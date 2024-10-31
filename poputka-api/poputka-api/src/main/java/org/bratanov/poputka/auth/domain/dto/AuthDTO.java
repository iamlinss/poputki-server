package org.bratanov.poputka.auth.domain.dto;

public class AuthDTO {
    public record LoginRequest(String email, String password) {
    }

    public record Response(String token) {
    }
}
