package com.example.prometei.api.request;

import com.example.prometei.api.enums.ModelType;
import com.example.prometei.api.enums.MoodType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClassificationParams {
    @NotNull
    private MoodType moodType;
    @NotNull
    private ModelType modelType;
    @Email
    @NotNull
    private String email;
    @NotNull
    private String question;
}
