package com.figuras.modelo;

public abstract class Figura implements FiguraGeometrica {

    private final String nombre;

    protected Figura(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    protected static void validarMedida(double medida) {
        if (!Double.isFinite(medida) || medida <= 0) {
            throw new IllegalArgumentException(
                "La medida debe ser un número positivo y finito."
            );
        }
    }

    @Override
    public abstract double calcularArea();

    @Override
    public abstract double calcularPerimetro();
}