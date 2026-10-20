package com.figuras.servicio;
import java.io.IOException;
import java.lang.reflect.Modifier;

import org.springframework.stereotype.Service;

import com.figuras.modelo.FiguraGeometrica;

import groovy.lang.GroovyClassLoader;
import jakarta.annotation.PreDestroy;

@Service
public class CargadorGroovy {

    private final FiguraFactory figuraFactory;
    private final GroovyClassLoader cargador;

    public CargadorGroovy(FiguraFactory figuraFactory) {
        this.figuraFactory = figuraFactory;

        this.cargador = new GroovyClassLoader(
            FiguraGeometrica.class.getClassLoader()
        );
    }

    public synchronized String cargar(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                "Debes enviar el código de una figura."
            );
        }

        Class<?> clase;

        try {
            clase = cargador.parseClass(codigo);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(
                "No se pudo compilar el código Groovy: " + e.getMessage(),
                e
            );
        }

        if (!FiguraGeometrica.class.isAssignableFrom(clase)) {
            throw new IllegalArgumentException(
                "La clase debe implementar FiguraGeometrica."
            );
        }

        if (!Modifier.isPublic(clase.getModifiers())
                || Modifier.isAbstract(clase.getModifiers())
                || clase.isInterface()) {
            throw new IllegalArgumentException(
                "La figura debe ser una clase pública y concreta."
            );
        }

        Class<? extends FiguraGeometrica> claseFigura =
            clase.asSubclass(FiguraGeometrica.class);

        // La fábrica comprueba que exista el constructor público Map.
        figuraFactory.registrar(claseFigura);

        return claseFigura.getSimpleName();
    }

    @PreDestroy
    public void cerrar() throws IOException {
        cargador.close();
    }
}