package com.example.prometei.models;

import com.example.prometei.models.enums.UserGender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="unauth_users")
public class UnauthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @Size(min = 5, max = 256)
    @Email
    private String email;
    private UserGender gender;
    private String firstName;
    private String lastName;
    @Size(min = 11)
    private String phoneNumber;
    private LocalDate birthDate;
    private String passport;
    private String internationalPassportNum;
    private LocalDate internationalPassportDate;

    @OneToMany(mappedBy = "unauthUser", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Purchase> purchases;

    @OneToMany(mappedBy = "unauthUser", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Ticket> tickets;
}
