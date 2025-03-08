package org.smirnova.poputka.domain.enums;

public enum TripStatus {
    CREATED("Создано"),
    CANCELLED("Отменено"),
    IN_PROGRESS("В пути"),
    COMPLETED("Завершено");

    private final String description;

    TripStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
