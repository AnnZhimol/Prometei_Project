package com.example.prometei.services;

import com.example.prometei.api.NeuralApi;
import com.example.prometei.api.enums.MoodType;
import com.example.prometei.api.response.PlacesByNeural;
import com.example.prometei.api.request.ClassificationParams;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NeuralService {
    private final NeuralApi neuralApi;
    private final Logger log = LoggerFactory.getLogger(NeuralService.class);

    public NeuralService(NeuralApi neuralApi) {
        this.neuralApi = neuralApi;
    }

    public String getClassification(ClassificationParams classificationParams) {
        log.info("Message from neural network received.");
        return neuralApi.getClassOfQuestion(classificationParams.getMoodType(), classificationParams.getModelType(), classificationParams.getEmail(), classificationParams.getQuestion()).getAnswer();
    }

    public List<PlacesByNeural> getTopPlaces(@NotNull MoodType mood,
                                             @NotNull String rubric) {
        log.info("Message with places from neural network received.");
        return neuralApi.getTopPlaces(mood, rubric);
    }
}
