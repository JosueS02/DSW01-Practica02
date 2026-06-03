package com.example.empleados.service;

import com.example.empleados.domain.EmpleadoEntity;
import com.example.empleados.domain.SesionAutenticada;
import com.example.empleados.repository.SesionAutenticadaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
public class AuthSessionService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final SesionAutenticadaRepository sesionAutenticadaRepository;
    private final int sessionHours;

    public AuthSessionService(
        SesionAutenticadaRepository sesionAutenticadaRepository,
        @Value("${app.auth.session-hours:8}") int sessionHours
    ) {
        this.sesionAutenticadaRepository = sesionAutenticadaRepository;
        this.sessionHours = sessionHours;
    }

    @Transactional
    public SesionAutenticada createSession(EmpleadoEntity empleado) {
        invalidateActiveSession(empleado.getClave());
        SesionAutenticada session = new SesionAutenticada();
        Instant now = Instant.now();
        session.setSessionId(generateSessionId());
        session.setEmpleado(empleado);
        session.setCreatedAt(now);
        session.setExpiresAt(now.plusSeconds(sessionHours * 60L * 60L));
        return sesionAutenticadaRepository.save(session);
    }

    @Transactional
    public void logout(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("Sesion no enviada");
        }
        SesionAutenticada session = sesionAutenticadaRepository.findBySessionIdAndInvalidatedAtIsNull(sessionId)
            .orElseThrow(() -> new SecurityException("Sesion invalida"));
        if (session.getExpiresAt().isBefore(Instant.now())) {
            throw new SecurityException("Sesion expirada");
        }
        session.setInvalidatedAt(Instant.now());
        sesionAutenticadaRepository.save(session);
    }

    @Transactional(readOnly = true)
    public Optional<SesionAutenticada> validateSession(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return Optional.empty();
        }
        Optional<SesionAutenticada> session = sesionAutenticadaRepository.findBySessionIdAndInvalidatedAtIsNull(sessionId);
        if (session.isEmpty()) {
            return Optional.empty();
        }
        if (session.get().getExpiresAt().isBefore(Instant.now())) {
            return Optional.empty();
        }
        return session;
    }

    @Transactional
    public void purgeExpiredSessions() {
        sesionAutenticadaRepository.deleteByExpiresAtBefore(Instant.now());
    }

    private void invalidateActiveSession(String empleadoClave) {
        sesionAutenticadaRepository.findByEmpleado_ClaveAndInvalidatedAtIsNull(empleadoClave).ifPresent(existing -> {
            existing.setInvalidatedAt(Instant.now());
            sesionAutenticadaRepository.save(existing);
        });
    }

    private String generateSessionId() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
