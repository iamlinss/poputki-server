package org.smirnova.poputka.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.entities.CityEntity;
import org.smirnova.poputka.services.CityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "City endpoints", description = "CRUD для работы с городами")
@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;
    private static final Logger log = LoggerFactory.getLogger(CityController.class);

    @Operation(summary = "Получить все города", description = "Возвращает список всех городов")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping
    public ResponseEntity<List<CityEntity>> getAllCities() {
        log.info("CALL: Get all cities");
        return ResponseEntity.ok(cityService.findAllCities());
    }

    @Operation(summary = "Создать город", description = "Создаёт новый город")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Город успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка запроса")
    })
    @PostMapping
    public ResponseEntity<CityEntity> createCity(@RequestBody CityEntity cityEntity) {
        log.info("CALL: Create city");
        return ResponseEntity.status(201).body(cityService.createCity(cityEntity));
    }

    @Operation(summary = "Обновить город", description = "Обновляет существующий город")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Город успешно обновлён"),
            @ApiResponse(responseCode = "404", description = "Город не найден")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<CityEntity> updateCity(@PathVariable Long id, @RequestBody CityEntity cityEntity) {
        log.info("CALL: Update city with id {}", id);
        Optional<CityEntity> updatedCity = cityService.updateCity(id, cityEntity);
        return updatedCity
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    @Operation(summary = "Удалить город", description = "Удаляет существующий город")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Город успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Город не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        log.info("CALL: Delete city with id {}", id);
        if (cityService.deleteCity(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(404).build();
    }
}
