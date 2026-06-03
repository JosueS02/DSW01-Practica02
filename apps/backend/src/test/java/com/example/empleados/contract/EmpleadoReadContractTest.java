package com.example.empleados.contract;

import com.example.empleados.controller.EmpleadoController;
import com.example.empleados.dto.DepartamentoResumenResponse;
import com.example.empleados.dto.EmpleadoResponse;
import com.example.empleados.service.EmpleadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmpleadoController.class)
class EmpleadoReadContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService empleadoService;

    @Test
    void shouldListEmployees() throws Exception {
        EmpleadoResponse response = new EmpleadoResponse();
        response.setClave("E-001");
        response.setNombre("Juan");
        response.setDireccion("Dir");
        response.setTelefono("123");
        DepartamentoResumenResponse departamento = new DepartamentoResumenResponse();
        departamento.setId("D-001");
        departamento.setNombre("General");
        response.setDepartamento(departamento);

        when(empleadoService.listAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/empleados").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].clave").value("E-001"))
            .andExpect(jsonPath("$[0].departamento.id").value("D-001"));
    }

    @Test
    void shouldGetEmployeeByClave() throws Exception {
        EmpleadoResponse response = new EmpleadoResponse();
        response.setClave("E-001");
        response.setNombre("Juan");
        response.setDireccion("Dir");
        response.setTelefono("123");
        DepartamentoResumenResponse departamento = new DepartamentoResumenResponse();
        departamento.setId("D-001");
        departamento.setNombre("General");
        response.setDepartamento(departamento);

        when(empleadoService.getByClave("E-001")).thenReturn(response);

        mockMvc.perform(get("/api/empleados/E-001").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clave").value("E-001"))
            .andExpect(jsonPath("$.departamento.id").value("D-001"));
    }
}
