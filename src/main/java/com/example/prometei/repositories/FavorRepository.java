package com.example.prometei.repositories;

import com.example.prometei.models.Favor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavorRepository extends JpaRepository<Favor, Long> {
}
