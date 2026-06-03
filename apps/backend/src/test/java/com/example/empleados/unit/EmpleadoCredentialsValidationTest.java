package com.example.empleados.unit;

import com.example.empleados.dto.CreateEmpleadoRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmpleadoCredentialsValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldFailWhenPasswordPolicyIsNotMet() {
        CreateEmpleadoRequest request = new CreateEmpleadoRequest();
        request.setNombre("Ana");
        request.setDireccion("Calle");
        request.setTelefono("555");
        request.setDepartamentoId("D-001");
        request.setEmail("ana@example.local");
        request.setPassword("password");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void shouldPassWithStrongPasswordAndValidEmail() {
        CreateEmpleadoRequest request = new CreateEmpleadoRequest();
        request.setNombre("Ana");
        request.setDireccion("Calle");
        request.setTelefono("555");
        request.setDepartamentoId("D-001");
        request.setEmail("ana@example.local");
        request.setPassword("Passw0rd");

        assertThat(validator.validate(request)).isEmpty();
    }
}
