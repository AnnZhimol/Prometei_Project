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
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @Column(nullable = false)
    private TicketType ticketType;
    @Column(nullable = false)
    private String seatNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Purchase purchase;

    @ManyToOne(fetch = FetchType.EAGER)
    private Flight flight;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<AdditionalFavor> additionalFavors;
}
