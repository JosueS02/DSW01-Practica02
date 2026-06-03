package com.example.empleados.unit;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClaveFormatValidatorTest {

    private static final Pattern PATTERN = Pattern.compile("^E-[0-9]{3,}$");

    @Test
    void shouldAcceptValidFormat() {
        assertTrue(PATTERN.matcher("E-001").matches());
        assertTrue(PATTERN.matcher("E-12345").matches());
    }

    @Test
    void shouldRejectInvalidFormat() {
        assertFalse(PATTERN.matcher("001").matches());
        assertFalse(PATTERN.matcher("E-1").matches());
        assertFalse(PATTERN.matcher("EMP-001").matches());
    }
}
