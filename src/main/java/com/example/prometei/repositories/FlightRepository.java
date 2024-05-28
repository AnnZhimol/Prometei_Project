package com.example.prometei.repositories;

import com.example.prometei.models.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    @Query("SELECT f FROM Flight f " +
            "WHERE (f.departurePoint = :departurePoint " +
            "AND f.destinationPoint = :destinationPoint " +
            "AND f.departureDate = :departureDate " +
            "AND f.economSeats >= :countEconomic " +
            "AND f.businessSeats >= :countBusiness) " +
            "OR (f.departurePoint = :destinationPoint " +
            "AND f.destinationPoint = :departurePoint " +
            "AND f.departureDate = :returnDate " +
            "AND f.economSeats >= :countEconomic " +
            "AND f.businessSeats >= :countBusiness)")
    List<Flight> findFlightsByPointsAndTime(String departurePoint,
                                            String destinationPoint,
                                            LocalDate departureDate,
                                            @Nullable LocalDate returnDate,
                                            Integer countBusiness,
                                            Integer countEconomic);

    @Query("SELECT f FROM Flight f " +
            "WHERE f.departureDate >= :departureDate " +
            "AND f.economSeats >= :countEconomic " +
            "AND f.businessSeats >= :countBusiness")
    List<Flight> findFlightsByInput(LocalDate departureDate,
                                    Integer countBusiness,
                                    Integer countEconomic);

}
