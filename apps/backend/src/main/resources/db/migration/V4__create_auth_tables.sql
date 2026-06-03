CREATE TABLE IF NOT EXISTS auth_sessions (
    session_id VARCHAR(64) PRIMARY KEY,
    empleado_clave VARCHAR(16) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    invalidated_at TIMESTAMP,
    CONSTRAINT fk_auth_sessions_empleado
      FOREIGN KEY (empleado_clave)
      REFERENCES empleados (clave)
      ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_auth_sessions_empleado ON auth_sessions (empleado_clave);
CREATE INDEX IF NOT EXISTS idx_auth_sessions_expires ON auth_sessions (expires_at);

CREATE TABLE IF NOT EXISTS auth_attempts (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    empleado_clave VARCHAR(16),
    resultado VARCHAR(16) NOT NULL,
    motivo VARCHAR(64) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_auth_attempts_empleado
      FOREIGN KEY (empleado_clave)
      REFERENCES empleados (clave)
      ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_auth_attempts_email_created_at ON auth_attempts (email, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_auth_attempts_created_at ON auth_attempts (created_at);
