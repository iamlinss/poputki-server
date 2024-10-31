package org.smirnova.poputka.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.dto.CarDto;
import org.smirnova.poputka.domain.dto.UserDto;
import org.smirnova.poputka.domain.dto.UserEditDto;
import org.smirnova.poputka.domain.entities.CarEntity;
import org.smirnova.poputka.domain.entities.UserEntity;
import org.smirnova.poputka.mappers.Mapper;
import org.smirnova.poputka.services.CarService;
import org.smirnova.poputka.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "User endpoints", description = "Работа с пользователем")
@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final Mapper<UserEntity, UserDto> userMapper;
    private final  Mapper<CarEntity, CarDto> carMapper;
    private final CarService carService;


    @Operation(
            description = "Получить список всех пользователей",
            summary = "Список всех пользователей",
            responses = {
                    @ApiResponse(
                            description = "Успешно",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Не авторизован/ Токен не валидный",
                            responseCode = "401",
                            content = { @Content(schema = @Schema()) }
                    )
            }
    )
    @GetMapping(path = "/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("CALL: Get all users");

        List<UserEntity> userEntityList = userService.findAll();
        List<UserDto> userDtoList = userEntityList.stream()
                .map(userMapper::mapTo)
                .toList();
        return new ResponseEntity<>(userDtoList, HttpStatus.OK);
    }

    @Operation(
            description = "Получить пользователя по ID",
            summary = "Пользователь по ID",
            responses = {
                    @ApiResponse(
                            description = "Успешно",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Не авторизован/ Токен не валидный",
                            responseCode = "401",
                            content = { @Content(schema = @Schema()) }
                    ),
                    @ApiResponse(
                            description = "Не найден",
                            responseCode = "404",
                            content = { @Content(schema = @Schema()) }
                    )
            }
    )
    @GetMapping(path = "/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        log.info("CALL: Get user with ID {}", id);

        Optional<UserEntity> foundUser = userService.findOne(id);
        return foundUser.map(userEntity -> {
            UserDto userDto = userMapper.mapTo(userEntity);
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(
            description = "Обновить поля пользователя по ID, при этом все не переданные станут NULL",
            summary = "Обновить пользователя",
            responses = {
                    @ApiResponse(
                            description = "Успешно",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Не авторизован/ Токен не валидный",
                            responseCode = "401",
                            content = { @Content(schema = @Schema()) }
                    ),
                    @ApiResponse(
                            description = "Не найден",
                            responseCode = "404",
                            content = { @Content(schema = @Schema()) }
                    )
            }
    )
    @PutMapping(path = "/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserEditDto userEdit) {
        log.info("CALL: Update user with ID {}", id);

        if (!userService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<UserEntity> foundUser = userService.findOne(id);
        if (foundUser.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        UserEntity user = foundUser.get();

        user.setBirthDate(userEdit.getBirthDate());
        user.setDescription(userEdit.getDescription());
        user.setFirstName(userEdit.getFirstName());
        user.setLastName(userEdit.getLastName());
        user.setPhone(userEdit.getPhone());
        UserEntity savedUserEntity = userService.save(user);
        return new ResponseEntity<>(userMapper.mapTo(savedUserEntity), HttpStatus.OK);
    }

    @Operation(
            description = "Добавить машину пользователя по ID",
            summary = "Добавить машину пользователя",
            responses = {
                    @ApiResponse(
                            description = "Добавлено",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "Не авторизован/ Токен не валидный",
                            responseCode = "401",
                            content = { @Content(schema = @Schema()) }
                    ),
                    @ApiResponse(
                            description = "Не найден",
                            responseCode = "404",
                            content = { @Content(schema = @Schema()) }
                    )
            }
    )
    @PostMapping(path = "/cars/{userId}")
    public ResponseEntity<CarDto> createCar(@RequestBody CarDto carDto, @PathVariable Long userId) {
        log.info("CALL: Create car with ID {}", userId);

        if (!userService.isExists(userId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<UserEntity> foundUser = userService.findOne(userId);
        if (!foundUser.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        UserEntity userEntity = foundUser.get();
        CarEntity carEntity = carMapper.mapFrom(carDto);
        CarEntity savedCarEntity = carService.save(carEntity, userEntity);
        return new ResponseEntity<>(carMapper.mapTo(savedCarEntity), HttpStatus.CREATED);
    }


    @Operation(
            description = "Удалить машину пользователя по ID",
            summary = "Удалить машину пользователя",
            responses = {
                    @ApiResponse(
                            description = "Удалено",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Не авторизован/ Токен не валидный",
                            responseCode = "401",
                            content = { @Content(schema = @Schema()) }
                    ),
                    @ApiResponse(
                            description = "Не найден",
                            responseCode = "404",
                            content = { @Content(schema = @Schema()) }
                    )
            }
    )
    @DeleteMapping(path = "cars/{id}")
    public ResponseEntity<CarDto> deleteCar(@PathVariable Long id) {
        log.info("CALL: Delete car with ID {}", id);

        if(!carService.isExists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<CarEntity> foundCar = carService.findOne(id);
        if (!foundCar.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        CarEntity carEntity = foundCar.get();
        carService.remove(carEntity);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
