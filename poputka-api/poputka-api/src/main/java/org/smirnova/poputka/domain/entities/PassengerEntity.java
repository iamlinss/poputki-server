package org.smirnova.poputka.domain.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.smirnova.poputka.domain.enums.PassengerStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "passenger")
public class PassengerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "passenger_id_seq")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private Long tripId;

    private Long userId;

    private int seats;

    @Enumerated(EnumType.STRING)
    private PassengerStatus status = PassengerStatus.PENDING_CONFIRMATION;
}
