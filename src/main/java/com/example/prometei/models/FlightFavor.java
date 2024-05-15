package com.example.prometei.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class FlightFavor {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Double cost;

    @ManyToOne(fetch = FetchType.LAZY)
    private Flight flight;

    @OneToMany(mappedBy = "flightFavor", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<AdditionalFavor> additionalFavors;
}
