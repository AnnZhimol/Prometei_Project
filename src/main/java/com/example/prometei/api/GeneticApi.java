package com.example.prometei.api;

import com.example.prometei.api.request.FlightGeneticDto;
import com.example.prometei.api.response.FlightFromGenetic;
import com.example.prometei.api.response.GeneticSearch;
import com.example.prometei.api.request.DataGenetic;
import com.example.prometei.dto.FlightDtos.FlightDto;
import com.example.prometei.models.Flight;
import com.example.prometei.models.enums.AirplaneModel;
import com.example.prometei.services.TransformDataService;
import com.example.prometei.services.baseServices.FlightService;
import com.example.prometei.utils.LocalDateAdapter;
import com.example.prometei.utils.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class GeneticApi {
    private HttpHeaders headers = new HttpHeaders();
    private final RestTemplate restTemplate = new RestTemplate();
    private final FlightService flightService;
    private final TransformDataService transformDataService;

    public GeneticApi(FlightService flightService, TransformDataService transformDataService) {
        this.flightService = flightService;
        this.transformDataService = transformDataService;
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    private void setHeaders() {
        headers = new HttpHeaders();
        headers.add("accept", "application/json");
        headers.add("Content-Type", "application/json");
    }

    public FlightDto convertToFlightDto(FlightFromGenetic flightFromGenetic, Boolean withPet) {
        Flight flight = flightService.getById(flightFromGenetic.getId());
        return transformDataService.transformToFlightDto(flight, withPet);
    }

    public List<GeneticSearch> getRoutes(String departurePoint,
                                         String destinationPoint,
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Nullable LocalDate returnDate,
                                         Integer countBusiness,
                                         Integer countEconomic,
                                         Boolean withPet,
                                         AirplaneModel model) {
        String url = "http://127.0.0.1:8000/flight/get/";

        setHeaders();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .serializeNulls()
                .setPrettyPrinting()
                .create();

        List<FlightGeneticDto> flightTo = flightService.getDataGeneticTo(departureDate, returnDate,
                        countBusiness, countEconomic, withPet, model)
                .stream().map(transformDataService::transformToFlightGeneticDto).toList();

        List<FlightGeneticDto> flightFrom = returnDate == null ? Collections.emptyList() :
                flightService.getDataGeneticFrom(returnDate, countBusiness, countEconomic, withPet, model)
                .stream().map(transformDataService::transformToFlightGeneticDto).toList();

        DataGenetic dataGenetic = new DataGenetic(departurePoint, destinationPoint, countBusiness,
                countEconomic, departureDate, returnDate, flightTo, flightFrom);

        HttpEntity<String> request = new HttpEntity<>(gson.toJson(dataGenetic), headers);

        try {
            ResponseEntity<String> response = restTemplate
                    .postForEntity(url, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return List.of(gson.fromJson(response.getBody(), GeneticSearch[].class));
            } else {
                return new ArrayList<>();
            }
        } catch (HttpClientErrorException.NotFound ex) {
            return new ArrayList<>();
        }
    }
}
