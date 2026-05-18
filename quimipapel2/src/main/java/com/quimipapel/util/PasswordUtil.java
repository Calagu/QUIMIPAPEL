package com.quimipapel.util;

import org.mindrot.jbcrypt.BCrypt;

/** Utilidad centralizada para crear y validar contraseñas. */
public class PasswordUtil {

    private PasswordUtil() {}

    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    public static boolean verify(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null || storedPassword.isBlank()) return false;
        try {
            if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
                return BCrypt.checkpw(plainPassword, storedPassword);
            }
        } catch (Exception ignored) {
            return false;
        }
        // Compatibilidad temporal con usuarios antiguos guardados en texto plano.
        return plainPassword.equals(storedPassword);
    }
}
