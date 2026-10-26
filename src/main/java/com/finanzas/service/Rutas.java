package com.finanzas.service;

import java.nio.file.Path;

/** Dónde guarda el programa su configuración y sus datos (nunca en el directorio actual). */
public final class Rutas {

    private Rutas() {
    }

    /** ~/.config/finanzas/reglas.json */
    public static Path reglas() {
        String xdg = System.getenv("XDG_CONFIG_HOME");
        Path base = xdg == null || xdg.isBlank() ? Path.of(System.getProperty("user.home"), ".config") : Path.of(xdg);
        return base.resolve("finanzas").resolve("reglas.json");
    }

    /** ~/.local/share/finanzas/importados.txt */
    public static Path registro() {
        String xdg = System.getenv("XDG_DATA_HOME");
        Path base = xdg == null || xdg.isBlank() ? Path.of(System.getProperty("user.home"), ".local", "share") : Path.of(xdg);
        return base.resolve("finanzas").resolve("importados.txt");
    }
}
