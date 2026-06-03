package com.example.empleados.repository;

import com.example.empleados.domain.SesionAutenticada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface SesionAutenticadaRepository extends JpaRepository<SesionAutenticada, String> {

    Optional<SesionAutenticada> findBySessionIdAndInvalidatedAtIsNull(String sessionId);

    Optional<SesionAutenticada> findByEmpleado_ClaveAndInvalidatedAtIsNull(String empleadoClave);

    void deleteByExpiresAtBefore(Instant cutoff);
}
