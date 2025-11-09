package com.proyecto.cabapro.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.proyecto.cabapro.model.ApiKey;
import com.proyecto.cabapro.repository.ApiKeyRepository;

@Service
public class ApiKeyService {

    private final ApiKeyRepository repository;

    @Value("${security.api.master-key}")
    private String masterKey;

    public ApiKeyService(ApiKeyRepository repository) {
        this.repository = repository;
    }

    public Optional<ApiKey> findByKey(String key) {

        if (key != null && key.equals(masterKey)) {
            ApiKey apiKey = new ApiKey();
            apiKey.setOwnerApp("MASTER_KEY");
            apiKey.setKeyValue(masterKey);
            return Optional.of(apiKey);
        }

        return repository.findByKeyValue(key);
    }

    public boolean isValid(String key) {
        if (key == null) return false;
        if (key.equals(masterKey)) return true;
        return repository.findByKeyValue(key).isPresent();
    }

    public ApiKey generateForApp(String appName) {
        ApiKey apiKey = new ApiKey();
        apiKey.setOwnerApp(appName);
        apiKey.setKeyValue(java.util.UUID.randomUUID().toString());
        return repository.save(apiKey);
    }

    public boolean existsForApp(String appName) {
        return repository.existsByOwnerApp(appName);
    }
}
