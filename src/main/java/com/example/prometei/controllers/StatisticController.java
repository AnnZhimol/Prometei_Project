package com.example.prometei.controllers;

import com.example.prometei.dto.HeatMap.AirplaneSeats;
import com.example.prometei.dto.Statistic.AgeTicketDto;
import com.example.prometei.dto.Statistic.PopularFavors;
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
    public ResponseEntity<List<AirplaneSeats>> getDataForHeatMap(@RequestParam String userId) {
        return new ResponseEntity<>(statisticService.getDataForHeatMap(decryptId(userId)), HttpStatus.OK);
    }

    @GetMapping("/ageTicket")
    public ResponseEntity<AgeTicketDto> getDataForAgeMap() {
        return new ResponseEntity<>(statisticService.getDataForAgeMap(), HttpStatus.OK);
    }

    @GetMapping("/popularFavor")
    public ResponseEntity<PopularFavors> getDataForAgeMap(@RequestParam Month month) {
        return new ResponseEntity<>(statisticService.getPopularFavorsByMonth(month), HttpStatus.OK);
    }
}
