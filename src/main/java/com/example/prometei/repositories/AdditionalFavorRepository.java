package com.example.prometei.repositories;

import com.example.prometei.models.AdditionalFavor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdditionalFavorRepository extends JpaRepository<AdditionalFavor, Long> {
    @Query("SELECT af FROM AdditionalFavor af " +
            "WHERE af.ticket.id = :id ")
    List<AdditionalFavor> findAdditionalFavorsByTicket(Long id);
}
