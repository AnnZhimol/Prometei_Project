package com.example.prometei.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @Column(nullable = false)
    private String value;
    @Column(nullable = false)
    private String label;
    @Column(nullable = false)
    private String timezone;
    @Column(nullable = false)
    private Double latitude;
    @Column(nullable = false)
    private Double longitude;
}
