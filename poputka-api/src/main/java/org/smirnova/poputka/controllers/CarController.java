package org.smirnova.poputka.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.dto.CarDto;
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

import java.util.Optional;

@Tag(name = "Cars endpoints", description = "Работа с машинами")
@RestController
@RequestMapping("/api/cars")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CarController {

    private static final Logger log = LoggerFactory.getLogger(CarController.class);

    private final Mapper<CarEntity, CarDto> carMapper;

    private final UserService userService;
    private final CarService carService;

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
                            content = {@Content(schema = @Schema())}
                    ),
                    @ApiResponse(
                            description = "Не найден",
                            responseCode = "404",
                            content = {@Content(schema = @Schema())}
                    )
            }
    )
    @PostMapping(path = "/{userId}")
    public ResponseEntity<CarDto> createCar(@RequestBody CarDto carDto, @PathVariable Long userId) {
        log.info("CALL: Create car with ID {}", userId);

        if (!userService.isExists(userId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<UserEntity> foundUser = userService.findOne(userId);
        if (foundUser.isEmpty()) {
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
                            content = {@Content(schema = @Schema())}
                    ),
                    @ApiResponse(
                            description = "Не найден",
                            responseCode = "404",
                            content = {@Content(schema = @Schema())}
                    )
            }
    )
    @DeleteMapping(path = "{id}")
    public ResponseEntity<CarDto> deleteCar(@PathVariable Long id) {
        log.info("CALL: Delete car with ID {}", id);

        if (!carService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<CarEntity> foundCar = carService.findOne(id);
        if (foundCar.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        CarEntity carEntity = foundCar.get();
        carService.remove(carEntity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/price")
    public ResponseEntity<Integer> calculatePrice(
            @RequestParam String cityFrom,
            @RequestParam String cityTo) {
        log.info("CALL: Calculate price for route {} -> {}", cityFrom, cityTo);
        Optional<Double> price = carService.calculatePrice(cityFrom, cityTo);
        return price.map(value -> new ResponseEntity<>(value.intValue(), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
