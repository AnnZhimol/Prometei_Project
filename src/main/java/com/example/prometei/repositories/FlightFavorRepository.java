package com.example.prometei.repositories;

import com.example.prometei.models.FlightFavor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightFavorRepository extends JpaRepository<FlightFavor, Long> {
}
