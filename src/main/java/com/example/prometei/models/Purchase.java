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
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @Column(nullable = false)
    private Double totalCost;
    @Column(nullable = false)
    private String paymentMethod; //make enum?
    @Column(nullable = false)
    private OffsetDateTime createDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Ticket> tickets;
}
