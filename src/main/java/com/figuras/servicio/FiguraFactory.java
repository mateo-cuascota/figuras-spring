package com.figuras.servicio;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.figuras.modelo.Circulo;
import com.figuras.modelo.FiguraGeometrica;
import com.figuras.modelo.Rectangulo;
import com.figuras.modelo.Triangulo;

@Service
public class FiguraFactory {

    private final Map<String, Class<? extends FiguraGeometrica>> clases =
            new ConcurrentHashMap<>();

    public FiguraFactory() {
        registrar(Circulo.class);
        registrar(Rectangulo.class);
        registrar(Triangulo.class);
    }

    // También permitirá registrar las clases cargadas con Groovy.
    public void registrar(Class<? extends FiguraGeometrica> clase) {
        try {
            clase.getConstructor(Map.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                "La figura debe tener un constructor público que reciba Map.",
                e
            );
        }

        String nombre = clase.getSimpleName();

        if (clases.putIfAbsent(nombre, clase) != null) {
            throw new IllegalArgumentException(
                "Ya existe una figura registrada con el nombre: " + nombre
            );
        }
    }

    public FiguraGeometrica crear(
            String nombre,
            Map<String, Double> parametros) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                "Debes indicar el nombre de la figura."
            );
        }

        Class<? extends FiguraGeometrica> clase = clases.get(nombre);

        if (clase == null) {
            throw new IllegalArgumentException(
                "Figura no registrada: " + nombre
            );
        }

        if (parametros == null) {
            throw new IllegalArgumentException(
                "Debes enviar los parámetros de la figura."
            );
        }

        try {
            return clase.getConstructor(Map.class)
                        .newInstance(parametros);

        } catch (InvocationTargetException e) {
            Throwable causa = e.getCause();

            if (causa instanceof IllegalArgumentException error) {
                throw error;
            }

            throw new IllegalStateException(
                "Falló el constructor de " + nombre,
                causa
            );

        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                "No se pudo crear la figura: " + nombre,
                e
            );
        }
    }

    /** Nombres de las figuras registradas, ordenados. */
    public List<String> nombres() {
        return clases.keySet().stream().sorted().toList();
    }
}
