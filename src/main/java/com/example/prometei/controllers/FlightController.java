package com.example.prometei.controllers;

import com.example.prometei.dto.FavorDto.CreateFlightFavorDto;
import com.example.prometei.dto.FlightDtos.*;
import com.example.prometei.dto.FavorDto.FlightFavorDto;
import com.example.prometei.dto.GeneticAlg.DataGenetic;
import com.example.prometei.models.Airport;
import com.example.prometei.models.Flight;
import com.example.prometei.models.FlightFavor;
import com.example.prometei.services.TransformDataService;
import com.example.prometei.services.baseServices.FlightService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.example.prometei.utils.CipherUtil.decryptId;

@CrossOrigin
@RestController
@RequestMapping("/flight")
public class FlightController {
    private final FlightService flightService;
    private final TransformDataService transformDataService;

    public FlightController(FlightService flightService, TransformDataService transformDataService) {
        this.flightService = flightService;
        this.transformDataService = transformDataService;
    }

    /**
     * Возвращает данные (рейсы), которые подаются на вход ГА.
     *
     * @param departurePoint точка отправления указанная пользователем
     * @param destinationPoint точка назначения указанная пользователем
     * @param departureDate дата отправления (в формате ISO.DATE) указанная пользователем
     * @param returnDate дата возврата (в формате ISO.DATE) указанная пользователем
     * @param countBusiness количество билетов бизнес-класса, которое необходимо пользователю
     * @param countEconomic количество билетов эконом-класса, которое необходимо пользователю
     * @param withPet найти полеты с возможностью перевозки животных
     * @return ResponseEntity с объектом DataGenetic, содержащим данные для поиска рейсов
     */
    @GetMapping("/getFlightData")
    public ResponseEntity<DataGenetic> getDataForSearch(@RequestParam String departurePoint,
                                                        @RequestParam String destinationPoint,
                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Nullable LocalDate returnDate,
                                                        @RequestParam Integer countBusiness,
                                                        @RequestParam Integer countEconomic,
                                                        @RequestParam Boolean withPet) {
        return new ResponseEntity<>(new DataGenetic(departurePoint,
                                                    destinationPoint,
                                                    countBusiness,
                                                    countEconomic,
                                                    departureDate,
                                                    returnDate,
                flightService.getDataGeneticTo(departureDate,
                                               returnDate,
                                               countBusiness,
                                               countEconomic,
                                               withPet)
                        .stream().map(transformDataService::transformToFlightGeneticDto).toList(),
                flightService.getDataGeneticFrom(returnDate,
                                                 countBusiness,
                                                 countEconomic,
                                                 withPet)
                        .stream().map(transformDataService::transformToFlightGeneticDto).toList()), HttpStatus.OK);
    }

    /**
     * Возвращает информацию о рейсе по заданному идентификатору.
     *
     * @param flightId зашифрованный идентификатор рейса
     * @return ResponseEntity с объектом FlightClientViewDto, содержащим данные о рейсе, или статусом NO_CONTENT, если рейс не найден
     */
    @GetMapping("/get")
    public ResponseEntity<FlightDto> getFlight(@RequestParam String flightId) {
        Flight flight = flightService.getById(decryptId(flightId));
        return flight == null
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(transformDataService.transformToFlightDto(flight, true), HttpStatus.OK);
    }

    /**
     * Возвращает список всех аэропортов.
     *
     * @return ResponseEntity со списком объектов Airport и статусом OK
     */
    @GetMapping("/getAirports")
    public ResponseEntity<List<Airport>> getAirports() {
        return new ResponseEntity<>(flightService.getAllAirports(), HttpStatus.OK);
    }

    /**
     * Ищет все возможные комбинации полетов, где destinationPoint одного полета совпадает с departurePoint другого полета.
     *
     * @param departurePoint точка отправления
     * @param destinationPoint точка назначения
     * @param departureDate дата отправления
     * @param returnDate дата возвращения (может быть null)
     * @param countBusiness количество бизнес-мест
     * @param countEconomic количество эконом-мест
     * @return список пар полетов, где destinationPoint одного полета совпадает с departurePoint другого полета
     */
    @GetMapping("/search")
    public ResponseEntity<List<SearchDto>> searchFlights(@RequestParam String departurePoint,
                                                         @RequestParam String destinationPoint,
                                                         @RequestParam Long departureDate,
                                                         @RequestParam @Nullable Long returnDate,
                                                         @RequestParam Integer countBusiness,
                                                         @RequestParam Integer countEconomic,
                                                         @RequestParam Boolean withPet,
                                                         @RequestParam Boolean useGeneticAlg) {
        LocalDate departureLocalDate = Instant.ofEpochMilli(departureDate).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate returnLocalDate = (returnDate != null) ? Instant.ofEpochSecond(returnDate).atZone(ZoneId.systemDefault()).toLocalDate() : null;


        if (!useGeneticAlg) {
            List<SearchDto> flightPairs = flightService.getSearchResult(departurePoint,
                    destinationPoint,
                    departureLocalDate,
                    returnLocalDate,
                    countBusiness,
                    countEconomic,
                    withPet).stream().map(pair -> transformDataService.transformToSearchDto(pair,withPet)).toList();

            List<SearchDto> result = new ArrayList<>(flightPairs);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            //TODO
            throw new NullPointerException("Function not exist");
        }
    }

    /**
     * Возвращает список всех рейсов.
     *
     * @return ResponseEntity со списком объектов FlightClientViewDto и статусом OK
     */
    @GetMapping("/all")
    public ResponseEntity<List<FlightDto>> getAllFlights() {
        return new ResponseEntity<>(flightService.getAll()
                                    .stream()
                                    .map(flight -> transformDataService.transformToFlightDto(flight, true))
                                    .toList(),
                                    HttpStatus.OK);
    }

    /**
     * Возвращает список дополнительных услуг для заданного рейса.
     *
     * @param flightId зашифрованный идентификатор рейса
     * @return ResponseEntity со списком объектов FlightFavorDto и статусом OK
     */
    @GetMapping("/getFlightFavors")
    public ResponseEntity<List<FlightFavorDto>> getFlightFavors(@RequestParam String flightId) {
        return new ResponseEntity<>(flightService.getFlightFavors(decryptId(flightId))
                .stream()
                .map(transformDataService::transformToFlightFavorDto)
                .toList(),
                HttpStatus.OK);
    }

    /**
     * Добавляет новый рейс на основе предоставленных данных и билеты, которые относятся к данному рейсу.
     *
     * @param createFlightDto объект, содержащий информацию о новом рейсе
     */
    @PostMapping("/create")
    public void addFlight(@RequestBody CreateFlightDto createFlightDto) {
        flightService.add(transformDataService.transformToFlight(createFlightDto));
    }

    /**
     * Добавляет дополнительные услуги (должны содержатся в общем перечне услуг, иначе ошибка) к указанному рейсу.
     *
     * @param flightId зашифрованный идентификатор рейса, к которому добавляются услуги
     * @param createFlightFavorDtos список объектов, содержащих информацию о дополнительных услугах
     */
    @PostMapping("/addFlightFavors")
    public void addFlightFavors(@RequestParam String flightId,
                                @RequestBody List<CreateFlightFavorDto> createFlightFavorDtos) {
        List<FlightFavor> listFavors = new ArrayList<>();

        for(CreateFlightFavorDto createFlightFavorDto : createFlightFavorDtos) {
            listFavors.add(transformDataService.transformToFlightFavor(createFlightFavorDto));
        }

        flightService.addFlightFavorsToFlight(decryptId(flightId), listFavors);
    }

    /**
     * Редактирует информацию о рейсе на основе предоставленных данных.
     * Если изменена модель самолета, старые билеты удаляются и добавляются новые.
     * При изменении даты отправления меняется и дата прибытия.
     *
     * @param flightId идентификатор рейса, который требуется отредактировать
     * @param createFlightDto объект, содержащий новую информацию о рейсе
     */
    @PatchMapping("/edit")
    public void editFlight(@RequestParam String flightId,
                           @RequestBody CreateFlightDto createFlightDto) {
        flightService.edit(decryptId(flightId), transformDataService.transformToFlight(createFlightDto));
    }
}
