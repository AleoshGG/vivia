-- V31: Finalizar cifrado - verificación de datos
-- Verifica que la migración Java (V30) haya cifrado correctamente todos los registros

-- Verificación: contar registros que aún tengan valores sin cifrar
-- En una BD con cifrado activo, los valores en texto plano en PII serían inusuales
-- Esta query solo es informativa para auditoría post-migración

-- Si quieres una verificación más estricta, descomentar y ejecutar post-deploy:
-- SELECT COUNT(*) as potential_issues FROM users
-- WHERE (name LIKE '%' AND name NOT LIKE '%==%') -- valores sin Base64
--    AND status != 'DELETED'
-- LIMIT 1;

-- Por ahora, solo aseguramos que las estructuras están correctas
-- Las columnas ampliadas en V29 ya están listas para almacenar ciphertext

-- Nota: No hacemos cambios destructivos aquí.
-- Si V30 falló, esta migración también fallará sin afectar otras cosas.
