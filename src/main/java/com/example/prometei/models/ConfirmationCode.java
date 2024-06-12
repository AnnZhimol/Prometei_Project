package com.example.prometei.models;

import com.example.prometei.models.enums.CodeState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class ConfirmationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private CodeState state;
    private LocalDateTime deadline;
    private LocalDateTime createDate;
    private String hash;

    @OneToOne(mappedBy = "confirmationCode", fetch = FetchType.EAGER)
    private User user;

    @OneToOne(mappedBy = "confirmationCode", fetch = FetchType.EAGER)
    private Ticket ticket;
}
