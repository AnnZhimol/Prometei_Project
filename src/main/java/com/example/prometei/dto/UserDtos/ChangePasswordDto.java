package com.example.prometei.dto.UserDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDto {
    @Size(min = 5, message = "Подтверждение должно быть длиной не менее 5-ти символов")
    @NotBlank(message = "Подтверждение не может быть пустым")
    private String confirmation;

    @Size(min = 10, message = "Пароль должен быть длиной не менее 10-ти символов")
    @NotBlank(message = "Пароль не может быть пустым")
    private String newPassword;

    @Size(min = 10, message = "Пароль должен быть длиной не менее 10-ти символов")
    @NotBlank(message = "Пароль не может быть пустым")
    private String passwordConfirm;
}
