package com.example.prometei.api;

import com.example.prometei.api.enums.ModelType;
import com.example.prometei.api.enums.MoodType;
import com.example.prometei.api.response.NeuralClassResponse;
import com.example.prometei.api.response.PlacesByNeural;
import com.google.gson.Gson;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class NeuralApi {
    private HttpHeaders headers = new HttpHeaders();
    private final RestTemplate restTemplate = new RestTemplate();

    public NeuralApi() {
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    private void setHeaders() {
        headers = new HttpHeaders();
        headers.add("accept", "application/json");
        headers.setContentType(MediaType.valueOf("text/csv; charset=UTF-8"));
    }

    public NeuralClassResponse getClassOfQuestion(@NotNull MoodType mood,
                                                  @NotNull ModelType model,
                                                  @NotNull @Email String email,
                                                  @NotNull String question) {
        String url = "http://127.0.0.1:8000/questions/add";

        setHeaders();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("type_mood", mood.name())
                .queryParam("type_model", model.name())
                .queryParam("email_user", email)
                .queryParam("question", question);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                request,
                String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return new Gson().fromJson(response.getBody(), NeuralClassResponse.class);
        } else {
            return null;
        }
    }

    public List<PlacesByNeural> getTopPlaces(@NotNull MoodType mood,
                                             @NotNull String rubric) {
        String urlTemplate  = "http://127.0.0.1:8000/reviews/{rubric}";

        setHeaders();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlTemplate)
                .queryParam("type_mood", mood.name());

        String url = builder.buildAndExpand(rubric).toUriString();

        HttpEntity<Map<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return List.of(new Gson().fromJson(response.getBody(), PlacesByNeural[].class));
        } else {
            return null;
        }
    }
}
