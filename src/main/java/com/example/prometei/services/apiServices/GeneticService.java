package com.example.prometei.services.apiServices;

import com.example.prometei.api.GeneticApi;
import com.example.prometei.api.response.GeneticSearch;
import com.example.prometei.dto.FlightDtos.FlightDto;
import com.example.prometei.dto.FlightDtos.SearchDto;
import com.example.prometei.models.enums.AirplaneModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeneticService {
    private final GeneticApi geneticApi;
    private final Logger log = LoggerFactory.getLogger(NeuralService.class);

    public GeneticService(GeneticApi geneticApi) {
        this.geneticApi = geneticApi;
    }

    /**
     * Возвращает список маршрутов (SearchDto) на основе заданных параметров.
     *
     * @param departurePoint Точка отправления.
     * @param destinationPoint Пункт назначения.
     * @param departureDate Дата отправления.
     * @param returnDate Дата возвращения (может быть null).
     * @param countBusiness Количество билетов бизнес-класса.
     * @param countEconomic Количество билетов эконом-класса.
     * @param withPet Указание, можно ли путешествовать с питомцем.
     * @param model Модель самолета.
     * @return Список объектов SearchDto, представляющих маршруты, соответствующие заданным параметрам.
     */
    @Cacheable(value = "routesCache", key = "{#departurePoint, #destinationPoint, #departureDate, #returnDate, #countBusiness, #countEconomic, #withPet, #model}", cacheManager = "cacheManager")
    public List<SearchDto> getRoutes(@RequestParam String departurePoint,
                                     @RequestParam String destinationPoint,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Nullable LocalDate returnDate,
                                     @RequestParam Integer countBusiness,
                                     @RequestParam Integer countEconomic,
                                     @RequestParam Boolean withPet,
                                     @RequestParam AirplaneModel model) {
        List<GeneticSearch> geneticSearches = geneticApi.getRoutes(departurePoint, destinationPoint,
                departureDate, returnDate, countBusiness, countEconomic, withPet, model);

        if (geneticSearches == null) {
            return new ArrayList<>();
        } else {
            log.info("Get flights by Genetic algorithm");
            return geneticSearches.stream()
                    .map(geneticSearch -> convertToSearchDto(geneticSearch, withPet))
                    .toList();
        }
    }

    private SearchDto convertToSearchDto(GeneticSearch geneticSearch, Boolean withPet) {
        List<FlightDto> toFlights = geneticSearch.getTo().stream()
                .map(flight -> geneticApi.convertToFlightDto(flight, withPet))
                .toList();

        List<FlightDto> backFlights = geneticSearch.getBack().stream()
                .map(flight -> geneticApi.convertToFlightDto(flight, withPet))
                .toList();

        return SearchDto.builder()
                .to(toFlights)
                .back(backFlights)
                .build();
    }
}
