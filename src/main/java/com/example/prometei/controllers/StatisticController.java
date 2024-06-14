package com.example.prometei.controllers;

import com.example.prometei.dto.Statistic.*;
import com.example.prometei.models.enums.AirplaneModel;
import com.example.prometei.services.StatisticService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Month;
import java.util.List;

import static com.example.prometei.utils.CipherUtil.decryptId;

@CrossOrigin
@RestController
@RequestMapping("/statistic")
public class StatisticController {
    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    /**
     * Выдает данные для тепловой карты мест в самолете.
     *
     * @param userId зашифрованный идентификатор пользователя
     * @return ответ с данными для тепловой карты мест в самолете
     */
    @GetMapping("/heatMap")
    public ResponseEntity<List<AirplaneSeats>> getDataForHeatMap(@RequestParam String userId,
                                                                 @RequestParam AirplaneModel airplaneModel) {
        return new ResponseEntity<>(statisticService.getDataForHeatMap(decryptId(userId), airplaneModel), HttpStatus.OK);
    }

    @GetMapping("/ageTicket")
    public ResponseEntity<AgeTicketDto> getDataForAgeMap() {
        return new ResponseEntity<>(statisticService.getDataForAgeMap(), HttpStatus.OK);
    }

    @GetMapping("/popularFavor")
    public ResponseEntity<FavorCount> getDataForPopularFavor(@RequestParam Month month,
                                                                @RequestParam int year) {
        return new ResponseEntity<>(statisticService.getPopularFavorsByMonth(year, month), HttpStatus.OK);
    }

    @GetMapping("/questionCount")
    public ResponseEntity<QuestionCount> getDataFromNeural() {
        return new ResponseEntity<>(statisticService.getDataFromNeural(), HttpStatus.OK);
    }

    @GetMapping("/averageCost")
    public ResponseEntity<AverageCost> getDataForAverageCost() {
        return new ResponseEntity<>(statisticService.calculateAverageCost(), HttpStatus.OK);
    }

    @GetMapping("/countSales")
    public ResponseEntity<List<DailyTicketSales>> getDataForDailyTicketSales(@RequestParam Month month,
                                                                             @RequestParam int year) {
        return new ResponseEntity<>(statisticService.calculateDailyTicketSales(year, month), HttpStatus.OK);
    }

    @GetMapping("/topRoute")
    public ResponseEntity<List<RouteStat>> getTopRoutes() {
        return new ResponseEntity<>(statisticService.calculateTopPopularRoutes(), HttpStatus.OK);
    }
}
