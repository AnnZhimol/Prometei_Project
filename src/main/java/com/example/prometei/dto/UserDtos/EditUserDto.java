package com.example.prometei.dto.UserDtos;

import com.example.prometei.models.enums.UserGender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for {@link com.example.prometei.models.User}
 */
@Data
@NoArgsConstructor
public class EditUserDto {
    private UserGender gender;
    private String firstName;
    private String lastName;
    @Size(min = 11)
    private String phoneNumber;
    @Email
    private String email;
    private LocalDate birthDate;
    private String passport;
    private String residenceCity;
    private String internationalPassportNum;
    private LocalDate internationalPassportDate;
}
