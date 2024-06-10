package com.example.prometei.controllers;

import com.example.prometei.api.enums.MoodType;
import com.example.prometei.api.response.PlacesByNeural;
import com.example.prometei.api.request.ClassificationParams;
import com.example.prometei.services.apiServices.NeuralService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/chat")
public class NeuralController {
    private final NeuralService neuralService;

    public NeuralController(NeuralService neuralService) {
        this.neuralService = neuralService;
    }

    @PostMapping("/classification")
    public ResponseEntity<String> getClassification(@RequestBody ClassificationParams classificationParams) {
        return new ResponseEntity<>(neuralService.getClassification(classificationParams), HttpStatus.OK);
    }

    @GetMapping("/places")
    public ResponseEntity<List<PlacesByNeural>> getPlaces(@NotNull MoodType mood,
                                                          @NotNull String rubric) {
        return new ResponseEntity<>(neuralService.getTopPlaces(mood, rubric), HttpStatus.OK);
    }

    @GetMapping("/negative")
    public ResponseEntity<List<String>> getNegative() {
        return new ResponseEntity<>(neuralService.getNegative(), HttpStatus.OK);
    }

    @GetMapping("/positive")
    public ResponseEntity<List<String>> getPositive() {
        return new ResponseEntity<>(neuralService.getPositive(), HttpStatus.OK);
    }
}
