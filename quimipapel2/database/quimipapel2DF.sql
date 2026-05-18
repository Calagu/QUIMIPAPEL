USE quimipapel;

UPDATE usuarios
SET password_hash = 'password',
    activo = 1
WHERE email IN (
    'carlos.fernandez@quimipapel.com',
    'maria.garcia@quimipapel.com',
    'miguel.fernandez@quimipapel.com',
    'ana.rodriguez@quimipapel.com',
    'pedro.gomez@quimipapel.com'
);