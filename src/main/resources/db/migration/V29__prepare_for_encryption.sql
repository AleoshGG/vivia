-- V29: Preparar esquema para cifrado de PII
-- Amplía las columnas que almacenarán ciphertext Base64
-- El email se mantiene sin cambios (no se cifra)

-- Ampliar columnas en tabla users para alojar ciphertext Base64
-- AES-256-GCM: IV(12) + ciphertext(variable) + tag(16) codificado en Base64 = ~33% más largo
ALTER TABLE users
    ALTER COLUMN name TYPE TEXT,
    ALTER COLUMN paternal_surname TYPE TEXT,
    ALTER COLUMN maternal_surname TYPE TEXT,
    ALTER COLUMN fcm_token TYPE TEXT;

-- Ampliar columnas en tabla lessors
ALTER TABLE lessors
    ALTER COLUMN phone_number TYPE TEXT;

-- Email se mantiene como VARCHAR(255) con UNIQUE - no se cifra por diseño
-- (simplifica autenticación y búsquedas)
