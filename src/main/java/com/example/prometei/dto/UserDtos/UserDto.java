package com.example.prometei.dto.UserDtos;

import com.example.prometei.models.enums.UserGender;
import com.example.prometei.models.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.example.prometei.models.User}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable {
    private String id;
    @Size(min = 5, max = 256)
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 10)
    private String password;
    private UserGender gender;
    private String firstName;
    private String lastName;
    @Size(min = 11)
    private String phoneNumber;
    private LocalDate birthDate;
    private String passport;
    private String residenceCity;
    private String internationalPassportNum;
    private LocalDate internationalPassportDate;
    private UserRole role;
}