package com.example.prometei.repositories;

import com.example.prometei.models.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    @Query("SELECT p FROM Purchase p " +
            "WHERE p.user.id = :id")
    List<Purchase> findPurchasesByUser(Long id);
}
