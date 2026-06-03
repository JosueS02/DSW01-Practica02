CREATE SEQUENCE IF NOT EXISTS departamento_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS departamentos (
    id VARCHAR(32) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

INSERT INTO departamentos (id, nombre, descripcion)
VALUES (CONCAT('D-', LPAD(nextval('departamento_id_seq')::text, 3, '0')), 'General', 'Departamento por defecto')
ON CONFLICT (nombre) DO NOTHING;

ALTER TABLE empleados
ADD COLUMN IF NOT EXISTS departamento_id VARCHAR(32);

UPDATE empleados
SET departamento_id = (SELECT id FROM departamentos WHERE nombre = 'General')
WHERE departamento_id IS NULL;

ALTER TABLE empleados
ALTER COLUMN departamento_id SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_empleados_departamento'
    ) THEN
        ALTER TABLE empleados
        ADD CONSTRAINT fk_empleados_departamento
        FOREIGN KEY (departamento_id)
        REFERENCES departamentos (id);
    END IF;
END $$;
