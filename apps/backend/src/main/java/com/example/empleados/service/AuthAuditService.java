package com.example.empleados.service;

import com.example.empleados.domain.EmpleadoEntity;
import com.example.empleados.domain.IntentoAutenticacion;
import com.example.empleados.repository.IntentoAutenticacionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthAuditService {

    private final IntentoAutenticacionRepository intentoAutenticacionRepository;
    private final int retentionDays;

    public AuthAuditService(
        IntentoAutenticacionRepository intentoAutenticacionRepository,
        @Value("${app.auth.audit.retention-days:90}") int retentionDays
    ) {
        this.intentoAutenticacionRepository = intentoAutenticacionRepository;
        this.retentionDays = retentionDays;
    }

    @Transactional
    public void registerAttempt(String email, EmpleadoEntity empleado, boolean success, String motivo) {
        IntentoAutenticacion intento = new IntentoAutenticacion();
        intento.setEmail(email);
        intento.setEmpleado(empleado);
        intento.setResultado(success ? IntentoAutenticacion.Resultado.SUCCESS : IntentoAutenticacion.Resultado.FAILURE);
        intento.setMotivo(motivo);
        intento.setCreatedAt(Instant.now());
        intentoAutenticacionRepository.save(intento);
    }

    @Transactional
    public void purgeExpiredAuditEntries() {
        Instant cutoff = Instant.now().minusSeconds(retentionDays * 24L * 60L * 60L);
        intentoAutenticacionRepository.deleteByCreatedAtBefore(cutoff);
    }
}
