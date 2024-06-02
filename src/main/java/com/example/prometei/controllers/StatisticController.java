package com.example.prometei.controllers;

import com.example.prometei.dto.HeatMap.AirplaneSeats;
import com.example.prometei.services.StatisticService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.prometei.utils.CipherUtil.decryptId;

@RestController
@RequestMapping("/statistic")
public class StatisticController {
    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping("/heatMap")
    public ResponseEntity<List<AirplaneSeats>> getDataForHeatMap(@RequestParam String userId) {
        return new ResponseEntity<>(statisticService.getDataForHeatMap(decryptId(userId)), HttpStatus.OK);
    }
}
