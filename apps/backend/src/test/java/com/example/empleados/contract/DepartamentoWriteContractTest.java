package com.example.empleados.contract;

import com.example.empleados.controller.DepartamentoController;
import com.example.empleados.dto.DepartamentoResponse;
import com.example.empleados.dto.UpdateDepartamentoRequest;
import com.example.empleados.service.DepartamentoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepartamentoController.class)
class DepartamentoWriteContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartamentoService departamentoService;

    @Test
    void shouldUpdateDepartamento() throws Exception {
        DepartamentoResponse response = new DepartamentoResponse();
        response.setId("D-001");
        response.setNombre("RH");
        response.setDescripcion("Actualizado");

        when(departamentoService.update(eq("D-001"), any(UpdateDepartamentoRequest.class))).thenReturn(response);

        String payload = """
            {
              \"nombre\": \"RH\",
              \"descripcion\": \"Actualizado\"
            }
            """;

        mockMvc.perform(put("/api/departamentos/D-001")
                .with(httpBasic("admin", "admin123"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("D-001"))
            .andExpect(jsonPath("$.nombre").value("RH"));
    }

    @Test
    void shouldDeleteDepartamento() throws Exception {
        doNothing().when(departamentoService).delete("D-001");

        mockMvc.perform(delete("/api/departamentos/D-001")
                .with(httpBasic("admin", "admin123"))
                .with(csrf()))
            .andExpect(status().isNoContent());
    }
}
