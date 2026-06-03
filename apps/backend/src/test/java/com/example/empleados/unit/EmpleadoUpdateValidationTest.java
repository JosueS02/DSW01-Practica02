package com.example.empleados.unit;

import com.example.empleados.dto.UpdateEmpleadoRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmpleadoUpdateValidationTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailWhenFieldsAreBlank() {
        UpdateEmpleadoRequest request = new UpdateEmpleadoRequest();
        request.setNombre(" ");
        request.setDireccion(" ");
        request.setTelefono(" ");
        request.setEmail(" ");
        request.setDepartamentoId(" ");

        Set<ConstraintViolation<UpdateEmpleadoRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenFieldsExceedMaxLength() {
        UpdateEmpleadoRequest request = new UpdateEmpleadoRequest();
        request.setNombre("a".repeat(101));
        request.setDireccion("b".repeat(101));
        request.setTelefono("c".repeat(101));
        request.setEmail("x".repeat(300) + "@example.local");
        request.setDepartamentoId("D-001");

        Set<ConstraintViolation<UpdateEmpleadoRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldPassForValidPayload() {
        UpdateEmpleadoRequest request = new UpdateEmpleadoRequest();
        request.setNombre("Nombre");
        request.setDireccion("Direccion");
        request.setTelefono("123456");
        request.setDepartamentoId("D-001");
        request.setEmail("valid@example.local");

        Set<ConstraintViolation<UpdateEmpleadoRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
