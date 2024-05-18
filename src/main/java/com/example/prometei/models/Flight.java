package com.example.prometei.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @Column(nullable = false)
    private String departurePoint;
    @Column(nullable = false)
    private String destinationPoint;
    @Column(nullable = false)
    private OffsetDateTime destinationTime;
    @Column(nullable = false)
    private OffsetDateTime departureTime;
    @Column(nullable = false)
    private Integer economSeats;
    @Column(nullable = false)
    private Integer businessSeats;
    @Column(nullable = false)
    private Double economyCost;
    @Column(nullable = false)
    private Double businessCost;
    @Column(nullable = false)
    private Integer airplaneNumber;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<FlightFavor> flightFavors;
}
