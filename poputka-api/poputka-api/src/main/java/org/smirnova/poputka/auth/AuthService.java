package org.smirnova.poputka.auth;


import lombok.AllArgsConstructor;
import org.smirnova.poputka.auth.domain.AuthUser;
import org.smirnova.poputka.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthService {

    private JwtEncoder jwtEncoder;
    private UserRepository userRepository;

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();

        String scope = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        AuthUser user = userRepository
                .findByEmail(authentication.getName())
                .map(AuthUser::new)
                .orElseThrow();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(String.valueOf(user.getUser().getId()))
                .issuedAt(now)
                .expiresAt(now.plus(10, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
