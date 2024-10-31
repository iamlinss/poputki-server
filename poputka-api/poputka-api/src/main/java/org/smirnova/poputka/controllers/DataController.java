package org.smirnova.poputka.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.entities.CityEntity;
import org.smirnova.poputka.domain.entities.StatusEntity;
import org.smirnova.poputka.services.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Data endpoints", description = "Полезные данные")
@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataController {

    private final DataService dataService;
    private static final Logger log = LoggerFactory.getLogger(DataController.class);

    @Operation(
            description = "Получить список всех городов",
            summary = "Список всех городов",
            responses = {
                    @ApiResponse(
                            description = "Успешно",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping(path = "/cities")
    public ResponseEntity<List<CityEntity>> getAllCities() {
        log.info("CALL: Get all cities");

        List<CityEntity> cityEntityList = dataService.findAllCities();
        return new ResponseEntity<>(cityEntityList, HttpStatus.OK);
    }

    @Operation(
            description = "Получить список всех статусов",
            summary = "Список всех статусов",
            responses = {
                    @ApiResponse(
                            description = "Успешно",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping(path = "/statuses")
    public ResponseEntity<List<StatusEntity>> getAllStatuses() {
        log.info("CALL: Get all statuses");

        List<StatusEntity> statusEntityList = dataService.findAllStatuses();
        return new ResponseEntity<>(statusEntityList, HttpStatus.OK);
    }



}
