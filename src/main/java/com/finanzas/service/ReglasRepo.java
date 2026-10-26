package com.finanzas.service;

import com.finanzas.model.Regla;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Reglas de categorías en un JSON editable a mano. */
public final class ReglasRepo {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Type LISTA = new TypeToken<List<Regla>>() { }.getType();

    private ReglasRepo() {
    }

    /**
     * Las reglas iniciales, armadas con los comercios del resumen de octubre de 2026.
     * Van en orden: gana la primera que coincide ("Retiro de dinero Carrefour" es efectivo, no supermercado).
     */
    public static List<Regla> porDefecto() {
        return List.of(
                new Regla("Retiro de dinero", "Efectivo"),
                new Regla("Rendimientos", "Rendimientos"),
                new Regla("Carrefour", "Supermercado"),
                new Regla("Autoservicio7030", "Supermercado"),
                new Regla("Verdulería", "Verdulería"),
                new Regla("ECOGAS", "Servicios"),
                new Regla("EPEC", "Servicios"),
                new Regla("Universidad Nacional", "Facultad"),
                new Regla("Grafica", "Facultad"));
    }

    /**
     * Lee las reglas. Si el archivo no existe, lo crea con {@link #porDefecto()} y devuelve esas.
     * Si existe pero no se puede leer, IllegalStateException (no se pisa: son las reglas del usuario).
     */
    public static List<Regla> cargar(Path archivo) throws IOException {
        if (!Files.exists(archivo)) {
            if (archivo.getParent() != null) Files.createDirectories(archivo.getParent());
            List<Regla> porDefecto = porDefecto();
            Files.writeString(archivo, GSON.toJson(porDefecto, LISTA), StandardCharsets.UTF_8);
            return porDefecto;
        }
        String json = Files.readString(archivo, StandardCharsets.UTF_8);
        try {
            List<Regla> reglas = GSON.fromJson(json, LISTA);
            if (reglas == null) throw new JsonParseException("archivo vacío");
            return List.copyOf(reglas);
        } catch (JsonParseException e) {
            throw new IllegalStateException("No se pudo leer " + archivo + ": " + e.getMessage(), e);
        }
    }
}
