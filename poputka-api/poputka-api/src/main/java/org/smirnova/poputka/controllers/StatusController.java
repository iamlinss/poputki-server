package org.smirnova.poputka.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.entities.StatusEntity;
import org.smirnova.poputka.services.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Status endpoints", description = "CRUD для работы со статусами")
@RestController
@RequestMapping("/api/statuses")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusService;
    private static final Logger log = LoggerFactory.getLogger(StatusController.class);

    @Operation(summary = "Получить все статусы", description = "Возвращает список всех статусов")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping
    public ResponseEntity<List<StatusEntity>> getAllStatuses() {
        log.info("CALL: Get all statuses");
        return ResponseEntity.ok(statusService.findAllStatuses());
    }

    @Operation(summary = "Создать статус", description = "Создаёт новый статус")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Статус успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка запроса")
    })
    @PostMapping
    public ResponseEntity<StatusEntity> createStatus(@RequestBody StatusEntity statusEntity) {
        log.info("CALL: Create status");
        return ResponseEntity.status(201).body(statusService.createStatus(statusEntity));
    }

    @Operation(summary = "Обновить статус", description = "Обновляет существующий статус")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус успешно обновлён"),
            @ApiResponse(responseCode = "404", description = "Статус не найден")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<StatusEntity> updateStatus(@PathVariable Long id, @RequestBody StatusEntity statusEntity) {
        log.info("CALL: Update status with id {}", id);
        Optional<StatusEntity> updatedStatus = statusService.updateStatus(id, statusEntity);
        return updatedStatus
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    @Operation(summary = "Удалить статус", description = "Удаляет существующий статус")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Статус успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Статус не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        log.info("CALL: Delete status with id {}", id);
        if (statusService.deleteStatus(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(404).build();
    }
}
