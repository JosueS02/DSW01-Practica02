package com.example.empleados.service;

import com.example.empleados.domain.EmpleadoEntity;
import com.example.empleados.domain.SesionAutenticada;
import com.example.empleados.dto.DepartamentoResumenResponse;
import com.example.empleados.dto.EmpleadoResponse;
import com.example.empleados.dto.LoginRequest;
import com.example.empleados.dto.LoginResponse;
import com.example.empleados.repository.EmpleadoRepository;
import com.example.empleados.security.PasswordHasher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class AuthService {

    private final EmpleadoRepository empleadoRepository;
    private final PasswordHasher passwordHasher;
    private final LockoutPolicyService lockoutPolicyService;
    private final AuthSessionService authSessionService;
    private final AuthAuditService authAuditService;

    public AuthService(
        EmpleadoRepository empleadoRepository,
        PasswordHasher passwordHasher,
        LockoutPolicyService lockoutPolicyService,
        AuthSessionService authSessionService,
        AuthAuditService authAuditService
    ) {
        this.empleadoRepository = empleadoRepository;
        this.passwordHasher = passwordHasher;
        this.lockoutPolicyService = lockoutPolicyService;
        this.authSessionService = authSessionService;
        this.authAuditService = authAuditService;
    }

    @Transactional
    public LoginResult login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());
        if (lockoutPolicyService.isLocked(normalizedEmail)) {
            authAuditService.registerAttempt(normalizedEmail, null, false, "LOCKED");
            throw new IllegalStateException("Cuenta temporalmente bloqueada");
        }

        EmpleadoEntity empleado = empleadoRepository.findByEmailIgnoreCase(normalizedEmail)
            .orElseThrow(() -> {
                authAuditService.registerAttempt(normalizedEmail, null, false, "INVALID_CREDENTIALS");
                return new SecurityException("Credenciales invalidas");
            });

        if (!empleado.isActivo()) {
            authAuditService.registerAttempt(normalizedEmail, empleado, false, "INACTIVE");
            throw new SecurityException("Empleado inactivo");
        }

        if (!passwordHasher.matches(request.getPassword(), empleado.getPasswordHash())) {
            authAuditService.registerAttempt(normalizedEmail, empleado, false, "INVALID_CREDENTIALS");
            throw new SecurityException("Credenciales invalidas");
        }

        SesionAutenticada session = authSessionService.createSession(empleado);
        authAuditService.registerAttempt(normalizedEmail, empleado, true, "SUCCESS");

        LoginResponse response = new LoginResponse();
        response.setEmpleado(toResponse(empleado));
        response.setSessionExpiresAt(session.getExpiresAt());
        response.setAuthMode("ui-session");

        return new LoginResult(response, session.getSessionId());
    }

    @Transactional
    public void logout(String sessionId) {
        authSessionService.logout(sessionId);
    }

    private EmpleadoResponse toResponse(EmpleadoEntity entity) {
        EmpleadoResponse response = new EmpleadoResponse();
        response.setClave(entity.getClave());
        response.setNombre(entity.getNombre());
        response.setDireccion(entity.getDireccion());
        response.setTelefono(entity.getTelefono());
        response.setEmail(entity.getEmail());
        DepartamentoResumenResponse departamento = new DepartamentoResumenResponse();
        departamento.setId(entity.getDepartamento().getId());
        departamento.setNombre(entity.getDepartamento().getNombre());
        response.setDepartamento(departamento);
        return response;
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    public static class LoginResult {
        private final LoginResponse response;
        private final String sessionId;

        public LoginResult(LoginResponse response, String sessionId) {
            this.response = response;
            this.sessionId = sessionId;
        }

        public LoginResponse getResponse() {
            return response;
        }

        public String getSessionId() {
            return sessionId;
        }
    }
}
