package com.figuras.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.figuras.modelo.Circulo;
import com.figuras.modelo.FiguraGeometrica;

/**
 * Requisito 1: instanciar cualquier figura a partir del nombre de la clase
 * y parámetros enviados en tiempo de ejecución.
 * Los valores esperados son los criterios de aceptación de las historias de usuario.
 */
@DisplayName("FiguraFactory: instanciar figuras por nombre y parámetros")
class FiguraFactoryTest {

    private static final double DELTA = 0.01;

    private FiguraFactory factory;

    @BeforeEach
    void preparar() {
        factory = new FiguraFactory();
    }

    @Test
    @DisplayName("(\"Circulo\", radio=5) → área 78.54 y perímetro 31.42")
    void creaCirculoPorNombre() {
        FiguraGeometrica figura = factory.crear("Circulo", Map.of("radio", 5.0));

        assertInstanceOf(Circulo.class, figura);
        assertEquals(78.54, figura.calcularArea(), DELTA);
        assertEquals(31.42, figura.calcularPerimetro(), DELTA);
    }

    @Test
    @DisplayName("(\"Rectangulo\", base=4, altura=6) → área 24 y perímetro 20")
    void creaRectanguloPorNombre() {
        FiguraGeometrica figura = factory.crear("Rectangulo",
                Map.of("base", 4.0, "altura", 6.0));

        assertEquals(24.0, figura.calcularArea(), DELTA);
        assertEquals(20.0, figura.calcularPerimetro(), DELTA);
    }

    @Test
    @DisplayName("(\"Triangulo\", 3, 4, 5) → área 6 (Herón) y perímetro 12")
    void creaTrianguloPorNombre() {
        FiguraGeometrica figura = factory.crear("Triangulo",
                Map.of("ladoA", 3.0, "ladoB", 4.0, "ladoC", 5.0));

        assertEquals(6.0, figura.calcularArea(), DELTA);
        assertEquals(12.0, figura.calcularPerimetro(), DELTA);
    }

    @Test
    @DisplayName("Solo las figuras básicas vienen registradas como nativas")
    void soloFigurasBasicasNativas() {
        assertEquals(List.of("Circulo", "Rectangulo", "Triangulo"), factory.nombres());
    }

    @Test
    @DisplayName("Rechaza medidas cero o negativas")
    void rechazaMedidaNoPositiva() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.crear("Circulo", Map.of("radio", -5.0)));
        assertThrows(IllegalArgumentException.class,
                () -> factory.crear("Rectangulo", Map.of("base", 0.0, "altura", 6.0)));
    }

    @Test
    @DisplayName("Rechaza si falta un parámetro obligatorio")
    void rechazaParametroFaltante() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> factory.crear("Circulo", Map.of()));

        assertTrue(error.getMessage().contains("radio"));
    }

    @Test
    @DisplayName("Rechaza lados que no cumplen la desigualdad triangular")
    void rechazaTrianguloImposible() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.crear("Triangulo",
                        Map.of("ladoA", 1.0, "ladoB", 2.0, "ladoC", 10.0)));
    }

    @Test
    @DisplayName("Rechaza una figura que no está registrada")
    void rechazaFiguraNoRegistrada() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> factory.crear("Pentagono", Map.of("lado", 4.0)));

        assertTrue(error.getMessage().contains("no registrada"));
    }
}
