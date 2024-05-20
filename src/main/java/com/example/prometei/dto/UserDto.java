package com.example.prometei.dto;

import com.example.prometei.models.UserGender;
import com.example.prometei.models.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.example.prometei.models.User}
 */
@Value
public class UserDto implements Serializable {
    long id;
    @Size(min = 5, max = 256)
    @Email
    String email;
    @Size(min = 10)
    String password;
    UserGender gender;
    String firstName;
    String lastName;
    @Size(min = 11)
    String phoneNumber;
    LocalDate birthDate;
    String passport;
    String residenceCity;
    String internationalPassportNum;
    LocalDate internationalPassportDate;
    UserRole role;
}