package com.example.empleados.integration;

import com.example.empleados.domain.DepartamentoEntity;
import com.example.empleados.repository.DepartamentoRepository;
import com.example.empleados.repository.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DepartamentoWriteIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    private String departamentoId;

    @BeforeEach
    void setup() {
        empleadoRepository.deleteAll();
        departamentoRepository.deleteAll();

        DepartamentoEntity departamento = new DepartamentoEntity();
        departamento.setId("D-090");
        departamento.setNombre("RH");
        departamento.setDescripcion("Inicial");
        departamentoId = departamentoRepository.save(departamento).getId();
    }

    @Test
    void shouldUpdateAndDeleteDepartamento() throws Exception {
        String updatePayload = """
            {
              \"nombre\": \"RH Actualizado\",
              \"descripcion\": \"Actualizado\"
            }
            """;

        mockMvc.perform(put("/api/departamentos/{id}", departamentoId)
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePayload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("RH Actualizado"));

        mockMvc.perform(delete("/api/departamentos/{id}", departamentoId).with(httpBasic("admin", "admin123")))
            .andExpect(status().isNoContent());
    }
}
