package com.example.prometei.models;

import com.example.prometei.models.enums.TicketType;
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

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    private UnauthUser unauthUser;

    @ManyToOne(fetch = FetchType.LAZY)
    private Purchase purchase;

    @ManyToOne(fetch = FetchType.EAGER)
    private Flight flight;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<AdditionalFavor> additionalFavors;
}
