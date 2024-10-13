package org.smirnova.poputka.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "trip")
public class TripEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trip_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private CityEntity departureLocation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private CityEntity destinationLocation;

    private LocalDateTime departureDateTime;

    private String description;

    private int seats;

    private String driverName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private StatusEntity status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private UserEntity user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private CarEntity car;

    private int price;
}
