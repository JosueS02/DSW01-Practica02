package com.example.empleados.unit;

import com.example.empleados.domain.DepartamentoEntity;
import com.example.empleados.domain.EmpleadoEntity;
import com.example.empleados.dto.CreateDepartamentoRequest;
import com.example.empleados.dto.CreateEmpleadoRequest;
import com.example.empleados.dto.DepartamentoResponse;
import com.example.empleados.dto.DepartamentoResumenResponse;
import com.example.empleados.dto.EmpleadoResponse;
import com.example.empleados.dto.UpdateDepartamentoRequest;
import com.example.empleados.dto.UpdateEmpleadoRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PojoCoverageTest {

    @Test
    void shouldReadWriteEmpleadoEntity() {
        DepartamentoEntity departamento = new DepartamentoEntity();
        departamento.setId("D-001");
        departamento.setNombre("General");

        EmpleadoEntity empleado = new EmpleadoEntity();
        empleado.setClave("E-001");
        empleado.setNombre("Ana");
        empleado.setDireccion("Calle");
        empleado.setTelefono("555");
        empleado.setEmail("ana@example.local");
        empleado.setPasswordHash("hash");
        empleado.setActivo(true);
        empleado.setDepartamento(departamento);

        assertThat(empleado.getClave()).isEqualTo("E-001");
        assertThat(empleado.getNombre()).isEqualTo("Ana");
        assertThat(empleado.getDireccion()).isEqualTo("Calle");
        assertThat(empleado.getTelefono()).isEqualTo("555");
        assertThat(empleado.getEmail()).isEqualTo("ana@example.local");
        assertThat(empleado.getPasswordHash()).isEqualTo("hash");
        assertThat(empleado.isActivo()).isTrue();
        assertThat(empleado.getDepartamento().getNombre()).isEqualTo("General");
    }

    @Test
    void shouldReadWriteDepartamentoEntity() {
        DepartamentoEntity departamento = new DepartamentoEntity();
        departamento.setId("D-002");
        departamento.setNombre("Ventas");
        departamento.setDescripcion("Area comercial");

        assertThat(departamento.getId()).isEqualTo("D-002");
        assertThat(departamento.getNombre()).isEqualTo("Ventas");
        assertThat(departamento.getDescripcion()).isEqualTo("Area comercial");
    }

    @Test
    void shouldReadWriteEmpleadoDto() {
        DepartamentoResumenResponse resumen = new DepartamentoResumenResponse();
        resumen.setId("D-003");
        resumen.setNombre("TI");

        EmpleadoResponse response = new EmpleadoResponse();
        response.setClave("E-003");
        response.setNombre("Luis");
        response.setDireccion("Calle 2");
        response.setTelefono("999");
        response.setEmail("luis@example.local");
        response.setDepartamento(resumen);

        assertThat(response.getClave()).isEqualTo("E-003");
        assertThat(response.getNombre()).isEqualTo("Luis");
        assertThat(response.getDireccion()).isEqualTo("Calle 2");
        assertThat(response.getTelefono()).isEqualTo("999");
        assertThat(response.getEmail()).isEqualTo("luis@example.local");
        assertThat(response.getDepartamento().getNombre()).isEqualTo("TI");
    }

    @Test
    void shouldReadWriteDepartamentoDtos() {
        CreateDepartamentoRequest create = new CreateDepartamentoRequest();
        create.setNombre("Marketing");
        create.setDescripcion("Campanas");

        UpdateDepartamentoRequest update = new UpdateDepartamentoRequest();
        update.setNombre("Marketing");
        update.setDescripcion("Campanas");

        DepartamentoResponse response = new DepartamentoResponse();
        response.setId("D-004");
        response.setNombre("Marketing");
        response.setDescripcion("Campanas");

        assertThat(create.getNombre()).isEqualTo("Marketing");
        assertThat(create.getDescripcion()).isEqualTo("Campanas");
        assertThat(update.getNombre()).isEqualTo("Marketing");
        assertThat(update.getDescripcion()).isEqualTo("Campanas");
        assertThat(response.getId()).isEqualTo("D-004");
        assertThat(response.getNombre()).isEqualTo("Marketing");
        assertThat(response.getDescripcion()).isEqualTo("Campanas");
    }

    @Test
    void shouldReadWriteEmpleadoRequests() {
        CreateEmpleadoRequest create = new CreateEmpleadoRequest();
        create.setNombre("Ana");
        create.setDireccion("Calle");
        create.setTelefono("555");
        create.setDepartamentoId("D-001");
        create.setEmail("ana@example.local");
        create.setPassword("Passw0rd");

        UpdateEmpleadoRequest update = new UpdateEmpleadoRequest();
        update.setNombre("Ana");
        update.setDireccion("Calle");
        update.setTelefono("555");
        update.setDepartamentoId("D-001");
        update.setEmail("ana@example.local");
        update.setPassword("Passw0rd");

        assertThat(create.getNombre()).isEqualTo("Ana");
        assertThat(create.getDireccion()).isEqualTo("Calle");
        assertThat(create.getTelefono()).isEqualTo("555");
        assertThat(create.getDepartamentoId()).isEqualTo("D-001");
        assertThat(create.getEmail()).isEqualTo("ana@example.local");
        assertThat(create.getPassword()).isEqualTo("Passw0rd");
        assertThat(update.getNombre()).isEqualTo("Ana");
        assertThat(update.getDireccion()).isEqualTo("Calle");
        assertThat(update.getTelefono()).isEqualTo("555");
        assertThat(update.getDepartamentoId()).isEqualTo("D-001");
        assertThat(update.getEmail()).isEqualTo("ana@example.local");
        assertThat(update.getPassword()).isEqualTo("Passw0rd");
    }
}
