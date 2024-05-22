package com.example.prometei.repositories;

import com.example.prometei.models.FlightFavor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightFavorRepository extends JpaRepository<FlightFavor, Long> {
    @Query("SELECT f FROM FlightFavor f " +
            "WHERE f.flight.id = :id ")
    List<FlightFavor> findFlightFavorsByFlight(Long id);
}
