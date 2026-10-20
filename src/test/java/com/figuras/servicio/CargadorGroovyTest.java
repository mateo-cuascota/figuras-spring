package com.figuras.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.figuras.modelo.Figura;
import com.figuras.modelo.FiguraGeometrica;

/**
 * Requisito 2: cargar y compilar código Groovy en tiempo de ejecución.
 * Una figura definida en Groovy debe funcionar como si fuera una clase nativa.
 */
@DisplayName("CargadorGroovy: figuras compiladas en tiempo de ejecución")
class CargadorGroovyTest {

    private static final double DELTA = 0.01;

    private static final String PENTAGONO = """
            import com.figuras.modelo.Figura

            class Pentagono extends Figura {
                private final double lado

                Pentagono(Map<String, Double> p) {
                    super("Pentagono")
                    if (p?.lado == null) throw new IllegalArgumentException("Debes enviar 'lado'.")
                    lado = p.lado as double
                    validarMedida(lado)
                }

                double calcularArea()      { (Math.sqrt(5 * (5 + 2 * Math.sqrt(5))) / 4) * lado * lado }
                double calcularPerimetro() { 5 * lado }
            }
            """;

    private FiguraFactory factory;
    private CargadorGroovy cargador;

    @BeforeEach
    void preparar() {
        factory = new FiguraFactory();
        cargador = new CargadorGroovy(factory);
    }

    @AfterEach
    void cerrar() throws Exception {
        cargador.cerrar();
    }

    @Test
    @DisplayName("Pentagono en Groovy se registra y calcula como una clase nativa")
    void pentagonoFuncionaComoNativo() {
        String nombre = cargador.cargar(PENTAGONO);

        assertEquals("Pentagono", nombre);
        assertTrue(factory.nombres().contains("Pentagono"));

        // Se crea con el mismo método que las figuras nativas.
        FiguraGeometrica figura = factory.crear("Pentagono", Map.of("lado", 4.0));

        assertInstanceOf(Figura.class, figura);       // hereda de la clase abstracta
        assertEquals(27.53, figura.calcularArea(), DELTA);
        assertEquals(20.0, figura.calcularPerimetro(), DELTA);
    }

    @Test
    @DisplayName("La figura Groovy reutiliza las validaciones heredadas de Figura")
    void groovyHeredaValidaciones() {
        cargador.cargar(PENTAGONO);

        assertThrows(IllegalArgumentException.class,
                () -> factory.crear("Pentagono", Map.of("lado", -1.0)));
    }

    @Test
    @DisplayName("Rechaza una clase que no implementa FiguraGeometrica")
    void rechazaClaseQueNoEsFigura() {
        String codigo = """
                class Persona {
                    Persona(Map p) { }
                }
                """;

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> cargador.cargar(codigo));

        assertTrue(error.getMessage().contains("FiguraGeometrica"));
    }

    @Test
    @DisplayName("Rechaza código Groovy con errores de sintaxis")
    void rechazaCodigoConErrores() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> cargador.cargar("class Roto extends { "));

        assertTrue(error.getMessage().contains("compilar"));
    }

    @Test
    @DisplayName("No permite registrar dos figuras con el mismo nombre")
    void rechazaNombreDuplicado() {
        cargador.cargar(PENTAGONO);

        assertThrows(IllegalArgumentException.class, () -> cargador.cargar(PENTAGONO));
    }

    @Test
    @DisplayName("Rechaza código vacío")
    void rechazaCodigoVacio() {
        assertThrows(IllegalArgumentException.class, () -> cargador.cargar("   "));
    }
}
