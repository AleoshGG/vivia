-- Otorga acceso premium permanente a todos los lessors existentes al momento del deploy.
-- Son usuarios de prueba previos a la introducción del gating; todos deben conservar acceso total.
-- premium_until = 2099-12-31 actúa como "sin expiración" para efectos prácticos.
INSERT INTO lessor_subscriptions (user_id, premium_until, updated_at)
SELECT
    user_id,
    TIMESTAMPTZ '2099-12-31 23:59:59+00',
    CURRENT_TIMESTAMP
FROM lessors
ON CONFLICT (user_id) DO NOTHING;
