package com.figuras.controlador;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejo centralizado de errores para TODOS los controladores.
 * Así ningún controlador repite su propio @ExceptionHandler (principio DRY)
 * y todas las respuestas de error tienen el mismo formato: {"error": "..."}.
 */
@RestControllerAdvice
public class ManejadorErrores {

    /** Datos enviados incorrectos: figura no registrada, medida negativa, Groovy inválido... → 400 */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> datosInvalidos(IllegalArgumentException e) {
        return responder(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /** El cuerpo de la petición no es un JSON válido → 400 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> jsonInvalido(HttpMessageNotReadableException e) {
        return responder(HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición no es un JSON válido.");
    }

    /** Falla interna, p. ej. el constructor de una figura Groovy lanzó otra excepción → 500 */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> errorInterno(IllegalStateException e) {
        String detalle = e.getCause() != null
                ? e.getMessage() + ": " + e.getCause().getMessage()
                : e.getMessage();
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, detalle);
    }

    private static ResponseEntity<Map<String, String>> responder(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado)
                .body(Map.of("error", String.valueOf(mensaje)));
    }
}
