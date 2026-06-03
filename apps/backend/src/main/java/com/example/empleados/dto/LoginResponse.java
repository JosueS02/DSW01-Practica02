package com.example.empleados.dto;

import java.time.Instant;

public class LoginResponse {

    private EmpleadoResponse empleado;
    private Instant sessionExpiresAt;
    private String authMode;

    public EmpleadoResponse getEmpleado() {
        return empleado;
    }

    public void setEmpleado(EmpleadoResponse empleado) {
        this.empleado = empleado;
    }

    public Instant getSessionExpiresAt() {
        return sessionExpiresAt;
    }

    public void setSessionExpiresAt(Instant sessionExpiresAt) {
        this.sessionExpiresAt = sessionExpiresAt;
    }

    public String getAuthMode() {
        return authMode;
    }

    public void setAuthMode(String authMode) {
        this.authMode = authMode;
    }
}
