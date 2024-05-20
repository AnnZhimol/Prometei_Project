package com.example.prometei.repositories;

import com.example.prometei.models.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    @Deprecated
    @Query("SELECT f FROM Flight f " +
            "WHERE f.departurePoint = :departurePoint " +
            "AND f.destinationPoint = :destinationPoint " +
            "AND f.departureTime = :departureTime")
    List<Flight> findFlightsByPointsAndTime(String departurePoint, String destinationPoint, OffsetDateTime departureTime);
}
