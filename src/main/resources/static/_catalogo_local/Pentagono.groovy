import com.figuras.modelo.Figura

// Pentágono regular. Parámetro: lado
class Pentagono extends Figura {

    private final double lado

    Pentagono(Map<String, Double> parametros) {
        super("Pentagono")
        if (parametros?.lado == null) {
            throw new IllegalArgumentException("Debes enviar el parámetro 'lado'.")
        }
        this.lado = parametros.lado as double
        validarMedida(this.lado)
    }

    @Override
    double calcularArea() {
        (Math.sqrt(5 * (5 + 2 * Math.sqrt(5))) / 4) * lado * lado
    }

    @Override
    double calcularPerimetro() {
        5 * lado
    }
}
