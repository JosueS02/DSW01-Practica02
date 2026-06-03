package com.example.empleados.contract;

import com.example.empleados.controller.DepartamentoController;
import com.example.empleados.dto.CreateDepartamentoRequest;
import com.example.empleados.dto.DepartamentoResponse;
import com.example.empleados.service.DepartamentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepartamentoController.class)
class DepartamentoReadCreateContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepartamentoService departamentoService;

    @Test
    void shouldCreateDepartamento() throws Exception {
        DepartamentoResponse response = new DepartamentoResponse();
        response.setId("D-001");
        response.setNombre("Recursos Humanos");
        response.setDescripcion("Gestion de talento");

        when(departamentoService.create(any(CreateDepartamentoRequest.class))).thenReturn(response);

        String payload = """
            {
              \"nombre\": \"Recursos Humanos\",
              \"descripcion\": \"Gestion de talento\"
            }
            """;

        mockMvc.perform(post("/api/departamentos")
                .with(httpBasic("admin", "admin123"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("D-001"))
            .andExpect(jsonPath("$.nombre").value("Recursos Humanos"));
    }

    @Test
    void shouldListDepartamentos() throws Exception {
        DepartamentoResponse response = new DepartamentoResponse();
        response.setId("D-001");
        response.setNombre("General");
        response.setDescripcion("Base");

        when(departamentoService.listAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/departamentos").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("D-001"))
            .andExpect(jsonPath("$[0].nombre").value("General"));
    }

    @Test
    void shouldRejectInvalidPayload() throws Exception {
        String payload = objectMapper.writeValueAsString(new CreateDepartamentoRequest());

        mockMvc.perform(post("/api/departamentos")
                .with(httpBasic("admin", "admin123"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest());
    }
}
