package com.example.empleados.contract;

import com.example.empleados.controller.EmpleadoController;
import com.example.empleados.dto.DepartamentoResumenResponse;
import com.example.empleados.dto.EmpleadoResponse;
import com.example.empleados.dto.UpdateEmpleadoRequest;
import com.example.empleados.service.EmpleadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmpleadoController.class)
class EmpleadoDepartamentoContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService empleadoService;

    @Test
    void shouldRequireDepartamentoAndReturnNestedDepartamento() throws Exception {
        EmpleadoResponse response = new EmpleadoResponse();
        response.setClave("E-001");
        response.setNombre("Ana");
        response.setDireccion("Calle 3");
        response.setTelefono("77777");
        response.setEmail("ana@example.local");
        DepartamentoResumenResponse departamento = new DepartamentoResumenResponse();
        departamento.setId("D-001");
        departamento.setNombre("General");
        response.setDepartamento(departamento);

        when(empleadoService.update(eq("E-001"), any(UpdateEmpleadoRequest.class))).thenReturn(response);

        String payload = """
            {
              \"nombre\": \"Ana\",
              \"direccion\": \"Calle 3\",
              \"telefono\": \"77777\",
                            \"departamentoId\": \"D-001\",
                            \"email\": \"ana@example.local\"
            }
            """;

        mockMvc.perform(put("/api/empleados/E-001")
                .with(httpBasic("admin", "admin123"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.departamento.id").value("D-001"))
            .andExpect(jsonPath("$.departamento.nombre").value("General"));
    }
}
