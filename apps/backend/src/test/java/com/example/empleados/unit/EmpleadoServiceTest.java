package com.example.empleados.unit;

import com.example.empleados.domain.DepartamentoEntity;
import com.example.empleados.domain.EmpleadoEntity;
import com.example.empleados.dto.CreateEmpleadoRequest;
import com.example.empleados.dto.UpdateEmpleadoRequest;
import com.example.empleados.repository.DepartamentoRepository;
import com.example.empleados.repository.EmpleadoRepository;
import com.example.empleados.security.PasswordHasher;
import com.example.empleados.service.ClaveEmpleadoGenerator;
import com.example.empleados.service.EmpleadoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private DepartamentoRepository departamentoRepository;

    @Mock
    private ClaveEmpleadoGenerator claveEmpleadoGenerator;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private EmpleadoService empleadoService;

    @Test
    void shouldCreateEmpleadoWithNormalizedFields() {
        CreateEmpleadoRequest request = new CreateEmpleadoRequest();
        request.setNombre(" Ana ");
        request.setDireccion(" Calle ");
        request.setTelefono(" 555 ");
        request.setEmail("ANA@EXAMPLE.LOCAL ");
        request.setPassword("Passw0rd");
        request.setDepartamentoId("D-001");

        DepartamentoEntity departamento = new DepartamentoEntity();
        departamento.setId("D-001");
        departamento.setNombre("General");

        when(claveEmpleadoGenerator.nextClave()).thenReturn("E-123");
        when(passwordHasher.hash("Passw0rd")).thenReturn("hashed");
        when(empleadoRepository.existsByEmailIgnoreCase("ana@example.local")).thenReturn(false);
        when(departamentoRepository.findById("D-001")).thenReturn(Optional.of(departamento));
        when(empleadoRepository.save(any(EmpleadoEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        empleadoService.create(request);

        ArgumentCaptor<EmpleadoEntity> captor = ArgumentCaptor.forClass(EmpleadoEntity.class);
        verify(empleadoRepository).save(captor.capture());
        EmpleadoEntity saved = captor.getValue();
        assertThat(saved.getClave()).isEqualTo("E-123");
        assertThat(saved.getNombre()).isEqualTo("Ana");
        assertThat(saved.getDireccion()).isEqualTo("Calle");
        assertThat(saved.getTelefono()).isEqualTo("555");
        assertThat(saved.getEmail()).isEqualTo("ana@example.local");
        assertThat(saved.getPasswordHash()).isEqualTo("hashed");
    }

    @Test
    void shouldRejectCreateWhenEmailAlreadyExists() {
        CreateEmpleadoRequest request = new CreateEmpleadoRequest();
        request.setNombre("Ana");
        request.setDireccion("Calle");
        request.setTelefono("555");
        request.setEmail("ana@example.local");
        request.setPassword("Passw0rd");
        request.setDepartamentoId("D-001");

        when(empleadoRepository.existsByEmailIgnoreCase("ana@example.local")).thenReturn(true);

        assertThatThrownBy(() -> empleadoService.create(request))
            .isInstanceOf(DataIntegrityViolationException.class);
        verify(empleadoRepository, never()).save(any(EmpleadoEntity.class));
    }

    @Test
    void shouldRejectUpdateWhenEmailDuplicated() {
        UpdateEmpleadoRequest request = new UpdateEmpleadoRequest();
        request.setNombre("Ana");
        request.setDireccion("Calle");
        request.setTelefono("555");
        request.setEmail("ana@example.local");
        request.setDepartamentoId("D-001");

        EmpleadoEntity existing = new EmpleadoEntity();
        existing.setClave("E-001");

        when(empleadoRepository.findById("E-001")).thenReturn(Optional.of(existing));
        when(empleadoRepository.existsByEmailIgnoreCaseAndClaveNot("ana@example.local", "E-001"))
            .thenReturn(true);

        assertThatThrownBy(() -> empleadoService.update("E-001", request))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldUpdatePasswordWhenProvided() {
        UpdateEmpleadoRequest request = new UpdateEmpleadoRequest();
        request.setNombre("Ana");
        request.setDireccion("Calle");
        request.setTelefono("555");
        request.setEmail("ana@example.local");
        request.setDepartamentoId("D-001");
        request.setPassword("Passw0rd");

        EmpleadoEntity existing = new EmpleadoEntity();
        existing.setClave("E-001");

        DepartamentoEntity departamento = new DepartamentoEntity();
        departamento.setId("D-001");

        when(empleadoRepository.findById("E-001")).thenReturn(Optional.of(existing));
        when(departamentoRepository.findById("D-001")).thenReturn(Optional.of(departamento));
        when(empleadoRepository.existsByEmailIgnoreCaseAndClaveNot("ana@example.local", "E-001"))
            .thenReturn(false);
        when(passwordHasher.hash("Passw0rd")).thenReturn("hashed");
        when(empleadoRepository.save(any(EmpleadoEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        empleadoService.update("E-001", request);

        verify(passwordHasher).hash("Passw0rd");
    }

    @Test
    void shouldRejectDeleteWhenEmpleadoMissing() {
        when(empleadoRepository.existsById("E-404")).thenReturn(false);

        assertThatThrownBy(() -> empleadoService.delete("E-404"))
            .isInstanceOf(NoSuchElementException.class);
    }
}
