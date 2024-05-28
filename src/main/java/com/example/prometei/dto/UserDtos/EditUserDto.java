package com.example.prometei.dto.UserDtos;

import com.example.prometei.models.User;
import com.example.prometei.models.enums.UserGender;
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
    private LocalDate birthDate;
    private String passport;
    private String residenceCity;
    private String internationalPassportNum;
    private LocalDate internationalPassportDate;

    public User dtoToEntity() {
        return User.builder()
                .birthDate(this.getBirthDate())
                .firstName(this.getFirstName())
                .lastName(this.getLastName())
                .gender(this.getGender())
                .internationalPassportNum(this.getInternationalPassportNum())
                .internationalPassportDate(this.getInternationalPassportDate())
                .passport(this.getPassport())
                .phoneNumber(this.getPhoneNumber())
                .residenceCity(this.getResidenceCity())
                .build();
    }
}
