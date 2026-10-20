package com.figuras.controlador;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.figuras.modelo.FiguraGeometrica;
import com.figuras.servicio.FiguraFactory;
import com.figuras.servicio.CargadorGroovy;

@RestController
@RequestMapping("/api/figuras")
public class FiguraController {

    private final FiguraFactory figuraFactory;
    private final CargadorGroovy cargadorGroovy;

    public FiguraController(
            FiguraFactory figuraFactory,
            CargadorGroovy cargadorGroovy) {
        this.figuraFactory = figuraFactory;
        this.cargadorGroovy = cargadorGroovy;
    }

    @PostMapping("/calcular")
    public ResultadoFigura calcular(
            @RequestBody SolicitudFigura solicitud) {

        FiguraGeometrica figura = figuraFactory.crear(
            solicitud.clase(),
            solicitud.parametros()
        );

        return new ResultadoFigura(
            figura.getClass().getSimpleName(),
            figura.calcularArea(),
            figura.calcularPerimetro()
        );
    }

    @PostMapping(
        value = "/cargar",
        consumes = "text/plain",
        produces = "application/json"
    )
    public Map<String, String> cargar(@RequestBody String codigo) {
        String nombre = cargadorGroovy.cargar(codigo);
        return Map.of(
            "mensaje", "Figura compilada y registrada correctamente.",
            "clase", nombre
        );
    }

    public record SolicitudFigura(
        String clase,
        Map<String, Double> parametros
    ) {}

    public record ResultadoFigura(
        String clase,
        double area,
        double perimetro
    ) {}
}