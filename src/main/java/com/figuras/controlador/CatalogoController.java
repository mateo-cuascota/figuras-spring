package com.figuras.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.figuras.servicio.FiguraFactory;
import com.figuras.servicio.ServicioFigurasRemotas;
import com.figuras.servicio.ServicioFigurasRemotas.ResultadoImportacion;

@RestController
@RequestMapping("/api/figuras")
public class CatalogoController {

    private final FiguraFactory figuraFactory;
    private final ServicioFigurasRemotas servicioRemoto;

    public CatalogoController(
            FiguraFactory figuraFactory,
            ServicioFigurasRemotas servicioRemoto) {
        this.figuraFactory = figuraFactory;
        this.servicioRemoto = servicioRemoto;
    }

    /** Lista todas las figuras registradas (nativas y Groovy). */
    @GetMapping
    public List<String> listar() {
        return figuraFactory.nombres();
    }

    /** Extra: trae las figuras nuevas desde el servicio externo. */
    @PostMapping("/importar")
    public Map<String, Object> importar() {
        List<ResultadoImportacion> resultados = servicioRemoto.importar();
        return Map.of(
            "origen", servicioRemoto.getUrlCatalogo(),
            "resultados", resultados,
            "registradas", figuraFactory.nombres()
        );
    }
}
