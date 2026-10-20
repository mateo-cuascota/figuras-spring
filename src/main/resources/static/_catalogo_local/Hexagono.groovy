import com.figuras.modelo.Figura

// Hexágono regular. Parámetro: lado
class Hexagono extends Figura {

    private final double lado

    Hexagono(Map<String, Double> parametros) {
        super("Hexagono")
        if (parametros?.lado == null) {
            throw new IllegalArgumentException("Debes enviar el parámetro 'lado'.")
        }
        this.lado = parametros.lado as double
        validarMedida(this.lado)
    }

    @Override
    double calcularArea() {
        (3 * Math.sqrt(3) / 2) * lado * lado
    }

    @Override
    double calcularPerimetro() {
        6 * lado
    }
}
