package org.smirnova.poputka.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.auth.domain.dto.AuthDTO;
import org.smirnova.poputka.auth.AuthService;
import org.smirnova.poputka.auth.domain.AuthUser;
import org.smirnova.poputka.domain.dto.UserDto;
import org.smirnova.poputka.domain.entities.UserEntity;
import org.smirnova.poputka.mappers.Mapper;
import org.smirnova.poputka.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Util endpoints", description = "Утильные штуки (Регистрация, Проверки)")
@RestController
@RequestMapping("/api/util")
@RequiredArgsConstructor
public class UtilController {

    private static final Logger log = LoggerFactory.getLogger(UtilController.class);

    private final AuthService authService;
    private final UserService userService;
    private final Mapper<UserEntity, UserDto> userMapper;
    private final AuthenticationManager authenticationManager;


    @Operation(
            description = "Регистрация пользователя",
            summary = "Регистрация",
            responses = {
                    @ApiResponse(
                            description = "Зарегистрирован",
                            responseCode = "201"
                    )
            }
    )
    @PostMapping(path = "/register")
    public ResponseEntity<Object> createUser(@RequestBody UserDto user) {
        log.info("CALL: Create user: {}", user);

        if (!(user.getEmail() != null && user.getEmail().matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"))) {
            return ResponseEntity.badRequest().body("Invalid email format");
        }

        if (userService.isEmailExists(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        UserEntity userEntity = userMapper.mapFrom(user);
        userEntity.setRate(0);
        userEntity.setTripAmount(0);
        userEntity.setRegistrationDate(LocalDate.now());
        UserEntity savedUserEntity = userService.save(userEntity);
        return new ResponseEntity<>(userMapper.mapTo(savedUserEntity), HttpStatus.CREATED);
    }

    @Operation(
            description = "Проверка EMAIL на уникальность",
            summary = "Проверка EMAIL",
            responses = {
                    @ApiResponse(
                            description = "Email найден",
                            responseCode = "302",
                            content = {@Content(schema = @Schema())}
                    ),
                    @ApiResponse(
                            description = "Email не найден",
                            responseCode = "401",
                            content = {@Content(schema = @Schema())}
                    )
            }
    )
    @GetMapping(path = "/checkEmail")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        log.info("CALL: Check email: {}", email);

        if (userService.isEmailExists(email)) {
            return new ResponseEntity<>(HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(
            description = "Авторизация",
            summary = "Авторизация",
            responses = {
                    @ApiResponse(
                            description = "Успешно",
                            responseCode = "200",
                            content = {@Content(schema = @Schema(implementation = AuthDTO.Response.class), mediaType = "application/json")}
                    ),
                    @ApiResponse(
                            description = "Не авторизован",
                            responseCode = "401"
                    )
            }
    )
    @Validated
    @PostMapping("/login")
    public ResponseEntity<AuthDTO.Response> login(@RequestBody AuthDTO.LoginRequest userLogin) {
        log.info("CALL: Login user: {}", userLogin);

        Authentication authentication;
        try {
            authentication =
                    authenticationManager
                            .authenticate(new UsernamePasswordAuthenticationToken(
                                    userLogin.email(),
                                    userLogin.password()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            AuthUser userDetails = (AuthUser) authentication.getPrincipal();

            log.info("Token generated for user: {}", userDetails.getUsername());

            String token = authService.generateToken(authentication);
            UserEntity userEntity = userDetails.getUser();
            UserDto userDto = userMapper.mapTo(userEntity);

            // Возвращаем токен и данные пользователя
            return ResponseEntity.ok(new AuthDTO.Response(token, userDto));

        } catch (Exception ex) {
            log.error("Login failed: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
