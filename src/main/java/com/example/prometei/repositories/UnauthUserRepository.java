package com.example.prometei.repositories;

import com.example.prometei.models.UnauthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnauthUserRepository extends JpaRepository<UnauthUser, Long> {
}
