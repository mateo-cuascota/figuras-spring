package com.figuras.modelo;
import java.util.Map;

public class Circulo extends Figura {

    private final double radio;

    public Circulo(Map<String, Double> parametros) {
        super("Circulo");

        if (parametros == null || parametros.get("radio") == null) {
            throw new IllegalArgumentException(
                "Debes enviar el parámetro 'radio'."
            );
        }

        this.radio = parametros.get("radio");
        validarMedida(this.radio);
    }

    @Override
    public double calcularArea() {
        return Math.PI * radio * radio;
    }

    @Override
    public double calcularPerimetro() {
        return 2 * Math.PI * radio;
    }
}