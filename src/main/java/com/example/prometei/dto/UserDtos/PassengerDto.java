package com.example.prometei.dto.UserDtos;

import com.example.prometei.models.enums.UserGender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PassengerDto implements Serializable {
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
}
