package com.qumi.app;

import com.qumi.app.model.Usuario;

public final class Session {
    private static Usuario currentUser;

    private Session() {}

    public static Usuario getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(Usuario user) {
        currentUser = user;
    }

    public static void clear() {
        currentUser = null;
    }
}
