package com.example.prometei.models;

import com.example.prometei.models.enums.AirplaneModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private LocalTime destinationTime;
    @Column(nullable = false)
    private LocalTime departureTime;
    @Column(nullable = false)
    private LocalDate destinationDate;
    @Column(nullable = false)
    private LocalDate departureDate;
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
    @Column(nullable = false)
    private AirplaneModel airplaneModel;
    @Column(nullable = false)
    private Double distance;
    @Column(nullable = false)
    private Double flightTime;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<FlightFavor> flightFavors;
}
