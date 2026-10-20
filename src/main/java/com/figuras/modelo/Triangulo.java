package com.figuras.modelo;

import java.util.Map;

public class Triangulo extends Figura {

    private final double ladoA;
    private final double ladoB;
    private final double ladoC;

    public Triangulo(Map<String, Double> parametros) {
        super("Triangulo");

        if (parametros == null
                || parametros.get("ladoA") == null
                || parametros.get("ladoB") == null
                || parametros.get("ladoC") == null) {
            throw new IllegalArgumentException(
                "Debes enviar 'ladoA', 'ladoB' y 'ladoC'."
            );
        }

        this.ladoA = parametros.get("ladoA");
        this.ladoB = parametros.get("ladoB");
        this.ladoC = parametros.get("ladoC");

        validarMedida(ladoA);
        validarMedida(ladoB);
        validarMedida(ladoC);

        if (ladoA + ladoB <= ladoC
                || ladoA + ladoC <= ladoB
                || ladoB + ladoC <= ladoA) {
            throw new IllegalArgumentException(
                "Los lados no forman un triángulo válido."
            );
        }
    }

    @Override
    public double calcularArea() {
        // Fórmula de Herón.
        double s = calcularPerimetro() / 2;

        return Math.sqrt(
            s * (s - ladoA) * (s - ladoB) * (s - ladoC)
        );
    }

    @Override
    public double calcularPerimetro() {
        return ladoA + ladoB + ladoC;
    }
}