package com.example.empleados.contract;

import com.example.empleados.controller.EmpleadoController;
import com.example.empleados.dto.CreateEmpleadoRequest;
import com.example.empleados.dto.DepartamentoResumenResponse;
import com.example.empleados.dto.EmpleadoResponse;
import com.example.empleados.service.EmpleadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmpleadoController.class)
class EmpleadoCreateContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmpleadoService empleadoService;

    @Test
    void shouldCreateEmployeeAndReturn201() throws Exception {
        EmpleadoResponse response = new EmpleadoResponse();
        response.setClave("E-001");
        response.setNombre("Juan Perez");
        response.setDireccion("Calle 1");
        response.setTelefono("123456");
        response.setEmail("juan@example.local");
        DepartamentoResumenResponse departamento = new DepartamentoResumenResponse();
        departamento.setId("D-001");
        departamento.setNombre("General");
        response.setDepartamento(departamento);

        when(empleadoService.create(any(CreateEmpleadoRequest.class))).thenReturn(response);

        String payload = """
            {
              \"nombre\": \"Juan Perez\",
              \"direccion\": \"Calle 1\",
                            \"telefono\": \"123456\",
                            \"departamentoId\": \"D-001\",
                            \"email\": \"juan@example.local\",
                            \"password\": \"Passw0rd\"
            }
            """;

        mockMvc.perform(post("/api/empleados")
                .with(httpBasic("admin", "admin123"))
            .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.clave").value("E-001"))
            .andExpect(jsonPath("$.departamento.id").value("D-001"));
    }

    @Test
    void shouldRejectInvalidPayload() throws Exception {
        String payload = objectMapper.writeValueAsString(new CreateEmpleadoRequest());

        mockMvc.perform(post("/api/empleados")
                .with(httpBasic("admin", "admin123"))
            .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest());
    }
}
