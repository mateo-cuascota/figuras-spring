import com.figuras.modelo.FiguraGeometrica

// Rombo. Parámetros: diagonalMayor, diagonalMenor
// Ejemplo de figura que implementa la interfaz directamente.
class Rombo implements FiguraGeometrica {

    private final double D
    private final double d

    Rombo(Map<String, Double> p) {
        if (p?.diagonalMayor == null || p?.diagonalMenor == null) {
            throw new IllegalArgumentException("Debes enviar 'diagonalMayor' y 'diagonalMenor'.")
        }
        D = p.diagonalMayor as double
        d = p.diagonalMenor as double
        if (D <= 0 || d <= 0) {
            throw new IllegalArgumentException("Las diagonales deben ser positivas.")
        }
    }

    double calcularArea()      { D * d / 2 }
    double calcularPerimetro() { 4 * Math.sqrt((D / 2) ** 2 + (d / 2) ** 2) }
}
