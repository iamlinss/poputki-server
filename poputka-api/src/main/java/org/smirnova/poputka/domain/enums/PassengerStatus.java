package org.smirnova.poputka.domain.enums;

public enum PassengerStatus {
    PENDING_CONFIRMATION("Ожидает подтверждения"),
    CONFIRMED("Подтверждён"),
    REJECTED_BY_DRIVER("Отклонён водителем"),
    CANCELLED_BY_PASSENGER("Отменён пассажиром");

    private final String description;

    PassengerStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
