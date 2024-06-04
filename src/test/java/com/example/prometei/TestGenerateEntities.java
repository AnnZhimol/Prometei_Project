package com.example.prometei;

import com.example.prometei.models.Airport;
import com.example.prometei.models.Favor;
import com.example.prometei.services.GenerateService;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.util.Arrays.asList;

@SpringBootTest
public class TestGenerateEntities {

    @Autowired
    GenerateService generateService;

    @Test
    public void FavorGenerate() throws FileNotFoundException {
        JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream("favors.json"), StandardCharsets.UTF_8));
        List<Favor> favors = asList(new Gson().fromJson(reader, Favor[].class));

        generateService.addAllFavors(favors);
    }

    @Test
    public void AirportGenerate() throws FileNotFoundException {
        JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream("airports.json"), StandardCharsets.UTF_8));
        List<Airport> airports = asList(new Gson().fromJson(reader, Airport[].class));

        generateService.addAllAirports(airports);
    }

    @Test
    public void UnAuthUserGenerate() {
        for (int i = 0; i < 8; i++) {
            generateService.generateUnAuthUser();
        }
    }

    @Test
    public void FlightGenerate() {
        for (int i = 0; i < 5; i++) {
            generateService.generateRandomFlight();
        }
    }

    @Test
    public void FlightFavorGenerate() {
        generateService.generateRandomFlightFavors();
    }

    @Test
    public void TicketGenerate() {
        generateService.generateAdditionalFavor();
    }

    @Test
    public void UserGenerate() {
        for (int i = 0; i < 5; i++) {
            generateService.generateUser();
        }
    }

    @Test
    public void PurchaseGenerate() {
        for (int i = 0; i < 10; i++) {
            generateService.generatePurchase();
        }
    }
}

