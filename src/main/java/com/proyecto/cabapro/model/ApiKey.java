package com.proyecto.cabapro.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String keyValue;

    private String ownerApp;

    private boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    public static ApiKey generate(String ownerApp) {
        ApiKey apiKey = new ApiKey();
        apiKey.keyValue = UUID.randomUUID().toString().replace("-", "");
        apiKey.ownerApp = ownerApp;
        return apiKey;
    }

    // --- Getters ---
    public String getKeyValue() { return keyValue; }
    public boolean isActive() { return active; }
    public String getOwnerApp() { return ownerApp; }
    public Long getId() { return id; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // --- Setters ---
    public void setKeyValue(String keyValue) { this.keyValue = keyValue; }
    public void setActive(boolean active) { this.active = active; }
    public void setOwnerApp(String ownerApp) { this.ownerApp = ownerApp; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
