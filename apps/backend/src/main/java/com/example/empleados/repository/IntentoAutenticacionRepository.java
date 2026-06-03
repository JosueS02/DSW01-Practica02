package com.example.empleados.repository;

import com.example.empleados.domain.IntentoAutenticacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface IntentoAutenticacionRepository extends JpaRepository<IntentoAutenticacion, Long> {

    List<IntentoAutenticacion> findTop10ByEmailOrderByCreatedAtDesc(String email);

    void deleteByCreatedAtBefore(Instant cutoff);
}
