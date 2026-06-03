package com.example.empleados.unit;

import com.example.empleados.domain.DepartamentoEntity;
import com.example.empleados.dto.CreateDepartamentoRequest;
import com.example.empleados.dto.UpdateDepartamentoRequest;
import com.example.empleados.repository.DepartamentoRepository;
import com.example.empleados.repository.EmpleadoRepository;
import com.example.empleados.service.DepartamentoIdGenerator;
import com.example.empleados.service.DepartamentoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartamentoServiceTest {

    @Mock
    private DepartamentoRepository departamentoRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private DepartamentoIdGenerator departamentoIdGenerator;

    @InjectMocks
    private DepartamentoService departamentoService;

    @Test
    void shouldRejectCreateWhenNombreDuplicated() {
        CreateDepartamentoRequest request = new CreateDepartamentoRequest();
        request.setNombre("General");

        when(departamentoRepository.existsByNombreIgnoreCase("General")).thenReturn(true);

        assertThatThrownBy(() -> departamentoService.create(request))
            .isInstanceOf(DataIntegrityViolationException.class);
        verify(departamentoRepository, never()).save(any(DepartamentoEntity.class));
    }

    @Test
    void shouldRejectUpdateWhenNombreDuplicated() {
        UpdateDepartamentoRequest request = new UpdateDepartamentoRequest();
        request.setNombre("General");

        DepartamentoEntity entity = new DepartamentoEntity();
        entity.setId("D-001");

        when(departamentoRepository.findById("D-001")).thenReturn(Optional.of(entity));
        when(departamentoRepository.existsByNombreIgnoreCaseAndIdNot("General", "D-001"))
            .thenReturn(true);

        assertThatThrownBy(() -> departamentoService.update("D-001", request))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRejectDeleteWhenDepartamentoHasEmpleados() {
        DepartamentoEntity entity = new DepartamentoEntity();
        entity.setId("D-001");

        when(departamentoRepository.findById("D-001")).thenReturn(Optional.of(entity));
        when(empleadoRepository.existsByDepartamento_Id("D-001")).thenReturn(true);

        assertThatThrownBy(() -> departamentoService.delete("D-001"))
            .isInstanceOf(IllegalStateException.class);
        verify(departamentoRepository, never()).delete(any(DepartamentoEntity.class));
    }
}
