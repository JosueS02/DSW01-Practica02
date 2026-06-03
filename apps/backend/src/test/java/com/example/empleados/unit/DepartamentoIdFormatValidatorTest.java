package com.example.empleados.unit;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DepartamentoIdFormatValidatorTest {

    private static final Pattern PATTERN = Pattern.compile("^D-[0-9]{3,}$");

    @Test
    void shouldAcceptValidFormat() {
        assertTrue(PATTERN.matcher("D-001").matches());
        assertTrue(PATTERN.matcher("D-1000").matches());
    }

    @Test
    void shouldRejectInvalidFormat() {
        assertFalse(PATTERN.matcher("001").matches());
        assertFalse(PATTERN.matcher("D-1").matches());
        assertFalse(PATTERN.matcher("DEP-001").matches());
    }
}
