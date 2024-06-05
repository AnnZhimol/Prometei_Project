package com.example.prometei.models;

import com.example.prometei.models.enums.PaymentMethod;
import com.example.prometei.models.enums.PaymentState;
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
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private PaymentState state;
    private PaymentMethod method;
    private LocalDateTime deadline;
    private LocalDateTime paymentDate;
    private LocalDateTime createDate;
    private String hash;

    @OneToOne(mappedBy = "payment")
    private Purchase purchase;
}
