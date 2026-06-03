ALTER TABLE empleados
ADD COLUMN IF NOT EXISTS email VARCHAR(255),
ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255),
ADD COLUMN IF NOT EXISTS activo BOOLEAN DEFAULT TRUE;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

UPDATE empleados
SET email = CONCAT(lower(replace(nombre, ' ', '.')), '.', lower(clave), '@example.local')
WHERE email IS NULL;

UPDATE empleados
SET password_hash = crypt(gen_random_uuid()::text, gen_salt('bf'))
WHERE password_hash IS NULL;

UPDATE empleados
SET activo = TRUE
WHERE activo IS NULL;

ALTER TABLE empleados
ALTER COLUMN email SET NOT NULL,
ALTER COLUMN password_hash SET NOT NULL,
ALTER COLUMN activo SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE c.relname = 'ux_empleados_email_ci'
          AND n.nspname = 'public'
    ) THEN
        CREATE UNIQUE INDEX ux_empleados_email_ci ON empleados ((lower(email)));
    END IF;
END $$;
