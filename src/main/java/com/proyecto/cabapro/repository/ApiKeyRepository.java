package com.proyecto.cabapro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.cabapro.model.ApiKey;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByKeyValue(String keyValue);
    boolean existsByOwnerApp(String ownerApp);
}
