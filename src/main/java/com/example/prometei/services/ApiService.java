package com.example.prometei.services;

import com.example.prometei.api.NeuralApi;
import com.example.prometei.api.enums.ModelType;
import com.example.prometei.api.enums.MoodType;
import com.example.prometei.api.response.PlacesByNeural;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiService {
    private final NeuralApi neuralApi;
    private final Logger log = LoggerFactory.getLogger(ApiService.class);

    public ApiService(NeuralApi neuralApi) {
        this.neuralApi = neuralApi;
    }

    public String getClassification(@NotNull MoodType mood,
                                    @NotNull ModelType model,
                                    @NotNull @Email String email,
                                    @NotNull String question) {
        log.info("Message from neural network received.");
        return neuralApi.getClassOfQuestion(mood, model, email, question).getAnswer();
    }

    public List<PlacesByNeural> getTopPlaces(@NotNull MoodType mood,
                                             @NotNull String rubric) {
        log.info("Message with places from neural network received.");
        return neuralApi.getTopPlaces(mood, rubric);
    }
}
