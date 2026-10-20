package com.figuras.modelo;

import java.util.Map;

public class Rectangulo extends Figura {

    private final double base;
    private final double altura;

    public Rectangulo(Map<String, Double> parametros) {
        super("Rectangulo");

        if (parametros == null
                || parametros.get("base") == null
                || parametros.get("altura") == null) {
            throw new IllegalArgumentException(
                "Debes enviar los parámetros 'base' y 'altura'."
            );
        }

        this.base = parametros.get("base");
        this.altura = parametros.get("altura");

        validarMedida(this.base);
        validarMedida(this.altura);
    }

    @Override
    public double calcularArea() {
        return base * altura;
    }

    @Override
    public double calcularPerimetro() {
        return 2 * (base + altura);
    }
}