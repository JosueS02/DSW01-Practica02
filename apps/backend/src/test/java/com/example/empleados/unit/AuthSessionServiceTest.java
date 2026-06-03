package com.example.empleados.unit;

import com.example.empleados.domain.EmpleadoEntity;
import com.example.empleados.domain.SesionAutenticada;
import com.example.empleados.repository.SesionAutenticadaRepository;
import com.example.empleados.service.AuthSessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthSessionServiceTest {

    @Mock
    private SesionAutenticadaRepository sesionAutenticadaRepository;

    @Test
    void shouldCreateSessionWithExpiry() {
        AuthSessionService service = new AuthSessionService(sesionAutenticadaRepository, 2);
        EmpleadoEntity empleado = new EmpleadoEntity();
        empleado.setClave("E-001");

        when(sesionAutenticadaRepository.findByEmpleado_ClaveAndInvalidatedAtIsNull("E-001"))
            .thenReturn(Optional.empty());
        when(sesionAutenticadaRepository.save(any(SesionAutenticada.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        SesionAutenticada created = service.createSession(empleado);

        assertThat(created.getSessionId()).isNotBlank();
        assertThat(created.getExpiresAt()).isAfter(created.getCreatedAt());
    }

    @Test
    void shouldInvalidateExistingSessionBeforeCreatingNew() {
        AuthSessionService service = new AuthSessionService(sesionAutenticadaRepository, 1);
        EmpleadoEntity empleado = new EmpleadoEntity();
        empleado.setClave("E-002");

        SesionAutenticada existing = new SesionAutenticada();
        existing.setSessionId("OLD");
        existing.setEmpleado(empleado);
        existing.setCreatedAt(Instant.now().minusSeconds(60));
        existing.setExpiresAt(Instant.now().plusSeconds(3600));

        when(sesionAutenticadaRepository.findByEmpleado_ClaveAndInvalidatedAtIsNull("E-002"))
            .thenReturn(Optional.of(existing));
        when(sesionAutenticadaRepository.save(any(SesionAutenticada.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        service.createSession(empleado);

        assertThat(existing.getInvalidatedAt()).isNotNull();
    }

    @Test
    void shouldRejectLogoutWhenSessionIdIsBlank() {
        AuthSessionService service = new AuthSessionService(sesionAutenticadaRepository, 1);

        assertThatThrownBy(() -> service.logout(" "))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectLogoutWhenSessionExpired() {
        AuthSessionService service = new AuthSessionService(sesionAutenticadaRepository, 1);
        SesionAutenticada expired = new SesionAutenticada();
        expired.setSessionId("S-1");
        expired.setExpiresAt(Instant.now().minusSeconds(10));

        when(sesionAutenticadaRepository.findBySessionIdAndInvalidatedAtIsNull("S-1"))
            .thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> service.logout("S-1"))
            .isInstanceOf(SecurityException.class);
        verify(sesionAutenticadaRepository, never()).save(any(SesionAutenticada.class));
    }

    @Test
    void shouldReturnEmptyWhenSessionExpired() {
        AuthSessionService service = new AuthSessionService(sesionAutenticadaRepository, 1);
        SesionAutenticada expired = new SesionAutenticada();
        expired.setSessionId("S-2");
        expired.setExpiresAt(Instant.now().minusSeconds(10));

        when(sesionAutenticadaRepository.findBySessionIdAndInvalidatedAtIsNull("S-2"))
            .thenReturn(Optional.of(expired));

        assertThat(service.validateSession("S-2")).isEmpty();
    }

    @Test
    void shouldDeleteExpiredSessions() {
        AuthSessionService service = new AuthSessionService(sesionAutenticadaRepository, 1);

        service.purgeExpiredSessions();

        verify(sesionAutenticadaRepository).deleteByExpiresAtBefore(any(Instant.class));
    }
}
