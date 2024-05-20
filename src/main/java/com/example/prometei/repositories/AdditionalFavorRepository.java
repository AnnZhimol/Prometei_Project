package com.example.prometei.repositories;

import com.example.prometei.models.AdditionalFavor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalFavorRepository extends JpaRepository<AdditionalFavor, Long> {
}
