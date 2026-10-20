package com.figuras.servicio;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * Extra: consume un servicio externo (un catálogo JSON) para obtener
 * nuevas figuras escritas en Groovy, las compila y las registra.
 *
 * Formato del catálogo:
 * [
 *   { "nombre": "Pentagono", "url": "Pentagono.groovy" },
 *   { "nombre": "Hexagono",  "url": "https://otro-servidor/Hexagono.groovy" }
 * ]
 * Las URL relativas se resuelven contra la URL del catálogo.
 */
@Service
public class ServicioFigurasRemotas {

    private static final Logger log =
            LoggerFactory.getLogger(ServicioFigurasRemotas.class);

    private static final JsonMapper JSON = JsonMapper.builder().build();

    private final CargadorGroovy cargadorGroovy;
    private final RestClient http;
    private final String urlCatalogo;
    private final boolean cargarAlIniciar;

    public ServicioFigurasRemotas(
            CargadorGroovy cargadorGroovy,
            @Value("${figuras.remoto.catalogo-url:http://localhost:${server.port:8080}/catalogo/figuras.json}")
            String urlCatalogo,
            @Value("${figuras.remoto.cargar-al-iniciar:false}")
            boolean cargarAlIniciar) {

        this.cargadorGroovy = cargadorGroovy;
        this.http = RestClient.create();
        this.urlCatalogo = urlCatalogo;
        this.cargarAlIniciar = cargarAlIniciar;
    }

    /** Descarga el catálogo y carga cada figura. */
    public List<ResultadoImportacion> importar() {
        URI base = URI.create(urlCatalogo);
        List<FiguraRemota> catalogo;

        try {
            // Se lee como texto: servicios como GitHub (raw) responden
            // text/plain aunque el contenido sea JSON.
            byte[] json = http.get()
                    .uri(base)
                    .retrieve()
                    .body(byte[].class);
            catalogo = json == null
                    ? null
                    : JSON.readValue(json, new TypeReference<List<FiguraRemota>>() {});
        } catch (JacksonException e) {
            throw new IllegalArgumentException(
                "El catálogo de " + urlCatalogo + " no es un JSON válido: "
                + e.getOriginalMessage(), e);
        } catch (RestClientException e) {
            throw new IllegalArgumentException(
                "No se pudo consultar el servicio externo " + urlCatalogo
                + ": " + e.getMessage(), e);
        }

        List<ResultadoImportacion> resultados = new ArrayList<>();
        if (catalogo == null) {
            return resultados;
        }

        for (FiguraRemota figura : catalogo) {
            resultados.add(importarUna(base, figura));
        }
        return resultados;
    }

    private ResultadoImportacion importarUna(URI base, FiguraRemota figura) {
        String nombre = figura.nombre();

        try {
            if (figura.url() == null || figura.url().isBlank()) {
                throw new IllegalArgumentException("La entrada no tiene 'url'.");
            }

            URI urlCodigo = base.resolve(figura.url());
            byte[] bytes = http.get()
                    .uri(urlCodigo)
                    .retrieve()
                    .body(byte[].class);
            String codigo = bytes == null
                    ? null
                    : new String(bytes, StandardCharsets.UTF_8);

            String registrada = cargadorGroovy.cargar(codigo);
            return new ResultadoImportacion(registrada, "cargada", null);

        } catch (IllegalArgumentException | RestClientException e) {
            String mensaje = e.getMessage();
            String estado = mensaje != null && mensaje.startsWith("Ya existe")
                    ? "ya-registrada"
                    : "error";
            return new ResultadoImportacion(nombre, estado, mensaje);
        }
    }

    /** Si figuras.remoto.cargar-al-iniciar=true, importa al arrancar. */
    @EventListener(ApplicationReadyEvent.class)
    public void alIniciar() {
        if (!cargarAlIniciar) {
            return;
        }
        try {
            importar().forEach(r ->
                log.info("Figura remota {} -> {}", r.nombre(), r.estado()));
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
        }
    }

    public String getUrlCatalogo() {
        return urlCatalogo;
    }

    public record FiguraRemota(String nombre, String url) {}

    public record ResultadoImportacion(String nombre, String estado, String detalle) {}
}
