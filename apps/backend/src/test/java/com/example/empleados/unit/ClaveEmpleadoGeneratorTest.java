package com.example.empleados.unit;

import com.example.empleados.service.ClaveEmpleadoGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClaveEmpleadoGeneratorTest {

    @Test
    void shouldFormatSequenceWithPrefixAndPadding() {
        ClaveEmpleadoGenerator generator = new ClaveEmpleadoGenerator(null);

        assertEquals("E-001", generator.format(1));
        assertEquals("E-010", generator.format(10));
        assertEquals("E-123", generator.format(123));
    }
}
