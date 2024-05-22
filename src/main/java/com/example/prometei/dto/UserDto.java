package com.example.prometei.dto;

import com.example.prometei.models.User;
import com.example.prometei.models.UserGender;
import com.example.prometei.models.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.example.prometei.models.User}
 */
@Data
@NoArgsConstructor
public class UserDto implements Serializable {
    private long id;
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

    public UserDto(User user) {
        id = user.getId();
        this.birthDate = user.getBirthDate();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.gender = user.getGender();
        this.internationalPassportDate = user.getInternationalPassportDate();
        this.internationalPassportNum = user.getInternationalPassportNum();
        this.passport = user.getPassport();
        this.password = user.getPassword();
        this.phoneNumber = user.getPhoneNumber();
        this.residenceCity = user.getResidenceCity();
        this.role = user.getRole();
    }

    public User dtoToEntity() {
        return User.builder()
                .id(this.getId())
                .birthDate(this.getBirthDate())
                .email(this.getEmail())
                .firstName(this.getFirstName())
                .lastName(this.getLastName())
                .gender(this.getGender())
                .internationalPassportNum(this.getInternationalPassportNum())
                .internationalPassportDate(this.getInternationalPassportDate())
                .passport(this.getPassport())
                .password(this.getPassword())
                .phoneNumber(this.getPhoneNumber())
                .residenceCity(this.getResidenceCity())
                .role(this.getRole())
                .build();
    }
}