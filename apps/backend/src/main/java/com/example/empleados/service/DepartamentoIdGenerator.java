package com.example.empleados.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DepartamentoIdGenerator {

    private final JdbcTemplate jdbcTemplate;

    public DepartamentoIdGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String nextId() {
        Long nextValue = jdbcTemplate.queryForObject("SELECT nextval('departamento_id_seq')", Long.class);
        if (nextValue == null) {
            throw new IllegalStateException("No fue posible obtener el siguiente correlativo de departamento");
        }
        return format(nextValue);
    }

    String format(long sequence) {
        return "D-" + String.format("%03d", sequence);
    }
}
