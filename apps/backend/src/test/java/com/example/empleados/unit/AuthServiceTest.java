package com.example.empleados.unit;

import com.example.empleados.domain.DepartamentoEntity;
import com.example.empleados.domain.EmpleadoEntity;
import com.example.empleados.domain.SesionAutenticada;
import com.example.empleados.dto.LoginRequest;
import com.example.empleados.service.AuthAuditService;
import com.example.empleados.service.AuthService;
import com.example.empleados.service.AuthSessionService;
import com.example.empleados.service.LockoutPolicyService;
import com.example.empleados.repository.EmpleadoRepository;
import com.example.empleados.security.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private LockoutPolicyService lockoutPolicyService;

    @Mock
    private AuthSessionService authSessionService;

    @Mock
    private AuthAuditService authAuditService;

    @InjectMocks
    private AuthService authService;

    private EmpleadoEntity empleado;

    @BeforeEach
    void setup() {
        DepartamentoEntity departamento = new DepartamentoEntity();
        departamento.setId("D-001");
        departamento.setNombre("General");

        empleado = new EmpleadoEntity();
        empleado.setClave("E-001");
        empleado.setNombre("Ana");
        empleado.setDireccion("Calle");
        empleado.setTelefono("555");
        empleado.setEmail("ana@example.local");
        empleado.setPasswordHash("hashed");
        empleado.setActivo(true);
        empleado.setDepartamento(departamento);
    }

    @Test
    void shouldRejectLoginWhenAccountIsLocked() {
        LoginRequest request = new LoginRequest();
        request.setEmail(" Ana@Example.Local ");
        request.setPassword("Passw0rd");

        when(lockoutPolicyService.isLocked("ana@example.local")).thenReturn(true);

        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(IllegalStateException.class);

        verify(authAuditService).registerAttempt("ana@example.local", null, false, "LOCKED");
        verifyNoInteractions(empleadoRepository);
    }

    @Test
    void shouldRejectWhenUserDoesNotExist() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ana@example.local");
        request.setPassword("Passw0rd");

        when(lockoutPolicyService.isLocked("ana@example.local")).thenReturn(false);
        when(empleadoRepository.findByEmailIgnoreCase("ana@example.local")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(SecurityException.class);

        verify(authAuditService).registerAttempt("ana@example.local", null, false, "INVALID_CREDENTIALS");
    }

    @Test
    void shouldRejectInactiveUser() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ana@example.local");
        request.setPassword("Passw0rd");
        empleado.setActivo(false);

        when(lockoutPolicyService.isLocked("ana@example.local")).thenReturn(false);
        when(empleadoRepository.findByEmailIgnoreCase("ana@example.local")).thenReturn(Optional.of(empleado));

        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(SecurityException.class);

        verify(authAuditService).registerAttempt("ana@example.local", empleado, false, "INACTIVE");
    }

    @Test
    void shouldRejectWhenPasswordDoesNotMatch() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ana@example.local");
        request.setPassword("WrongPass1");

        when(lockoutPolicyService.isLocked("ana@example.local")).thenReturn(false);
        when(empleadoRepository.findByEmailIgnoreCase("ana@example.local")).thenReturn(Optional.of(empleado));
        when(passwordHasher.matches("WrongPass1", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(SecurityException.class);

        verify(authAuditService).registerAttempt("ana@example.local", empleado, false, "INVALID_CREDENTIALS");
    }

    @Test
    void shouldLoginAndCreateSession() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ana@example.local");
        request.setPassword("Passw0rd");

        SesionAutenticada session = new SesionAutenticada();
        session.setSessionId("SESSION-1");
        session.setExpiresAt(Instant.now().plusSeconds(3600));

        when(lockoutPolicyService.isLocked("ana@example.local")).thenReturn(false);
        when(empleadoRepository.findByEmailIgnoreCase("ana@example.local")).thenReturn(Optional.of(empleado));
        when(passwordHasher.matches("Passw0rd", "hashed")).thenReturn(true);
        when(authSessionService.createSession(empleado)).thenReturn(session);

        AuthService.LoginResult result = authService.login(request);

        assertThat(result.getSessionId()).isEqualTo("SESSION-1");
        assertThat(result.getResponse().getEmpleado().getClave()).isEqualTo("E-001");
        assertThat(result.getResponse().getEmpleado().getDepartamento().getNombre()).isEqualTo("General");
        verify(authAuditService).registerAttempt("ana@example.local", empleado, true, "SUCCESS");
    }

    @Test
    void shouldDelegateLogout() {
        authService.logout("SESSION-1");

        verify(authSessionService).logout("SESSION-1");
    }
}
