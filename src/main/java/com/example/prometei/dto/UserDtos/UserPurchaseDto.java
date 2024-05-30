package com.example.prometei.dto.UserDtos;

import com.example.prometei.models.User;
import com.example.prometei.models.enums.UserGender;
import jakarta.validation.constraints.Email;
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
public class UserPurchaseDto implements Serializable {
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

    public User dtoToEntity() {
        return User.builder()
                .email(this.getEmail())
                .birthDate(this.getBirthDate())
                .firstName(this.getFirstName())
                .lastName(this.getLastName())
                .gender(this.getGender())
                .internationalPassportNum(this.getInternationalPassportNum())
                .internationalPassportDate(this.getInternationalPassportDate())
                .passport(this.getPassport())
                .phoneNumber(this.getPhoneNumber())
                .build();
    }
}
