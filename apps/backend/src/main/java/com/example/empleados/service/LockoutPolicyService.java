package com.example.empleados.service;

import com.example.empleados.domain.IntentoAutenticacion;
import com.example.empleados.repository.IntentoAutenticacionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class LockoutPolicyService {

    private final IntentoAutenticacionRepository intentoAutenticacionRepository;
    private final int maxFailures;
    private final int lockoutMinutes;

    public LockoutPolicyService(
        IntentoAutenticacionRepository intentoAutenticacionRepository,
        @Value("${app.auth.lockout.max-failures:5}") int maxFailures,
        @Value("${app.auth.lockout.minutes:15}") int lockoutMinutes
    ) {
        this.intentoAutenticacionRepository = intentoAutenticacionRepository;
        this.maxFailures = maxFailures;
        this.lockoutMinutes = lockoutMinutes;
    }

    public boolean isLocked(String email) {
        List<IntentoAutenticacion> attempts = intentoAutenticacionRepository.findTop10ByEmailOrderByCreatedAtDesc(email);
        if (attempts.size() < maxFailures) {
            return false;
        }

        int failures = 0;
        Instant latestFailure = null;
        for (int i = 0; i < attempts.size() && failures < maxFailures; i++) {
            IntentoAutenticacion attempt = attempts.get(i);
            if (attempt.getResultado() == IntentoAutenticacion.Resultado.SUCCESS) {
                return false;
            }
            if (attempt.getResultado() == IntentoAutenticacion.Resultado.FAILURE) {
                failures++;
                if (latestFailure == null) {
                    latestFailure = attempt.getCreatedAt();
                }
            }
        }

        if (failures < maxFailures || latestFailure == null) {
            return false;
        }

        return latestFailure.plusSeconds(lockoutMinutes * 60L).isAfter(Instant.now());
    }
}
