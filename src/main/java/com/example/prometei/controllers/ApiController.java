package com.example.prometei.controllers;

import com.example.prometei.api.enums.ModelType;
import com.example.prometei.api.enums.MoodType;
import com.example.prometei.api.response.PlacesByNeural;
import com.example.prometei.services.ApiService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/chat")
public class ApiController {
    private final ApiService apiService;

    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @PostMapping("/classification")
    public ResponseEntity<String> getClassification(@NotNull @RequestParam MoodType mood,
                                                    @NotNull @RequestParam ModelType model,
                                                    @NotNull @RequestParam @Email String email,
                                                    @NotNull @RequestParam String question) {
        return new ResponseEntity<>(apiService.getClassification(mood, model, email, question), HttpStatus.OK);
    }

    @GetMapping("/places")
    public ResponseEntity<List<PlacesByNeural>> getPlaces(@NotNull MoodType mood,
                                                          @NotNull String rubric) {
        return new ResponseEntity<>(apiService.getTopPlaces(mood, rubric), HttpStatus.OK);
    }
}
