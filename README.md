# Figuras geométricas · Spring Boot + Groovy

Aplicación en **Java con Spring Boot** que instancia figuras geométricas a partir del
**nombre de la clase y parámetros enviados en tiempo de ejecución**, permite **compilar
nuevas figuras en Groovy** sin reiniciar y **consume un servicio externo** para obtener
figuras adicionales.

Está construida sobre el diseño de la actividad anterior: interfaz `FiguraGeometrica`,
clase abstracta `Figura` y las figuras básicas `Circulo`, `Rectangulo` y `Triangulo`.

---
## Cómo ejecutarlo

Requiere **Java 21**. No hace falta instalar Maven (se usa el wrapper incluido).

```bash
./mvnw spring-boot:run        # Git Bash / Linux / macOS
mvnw.cmd spring-boot:run      # CMD / PowerShell
```

Al arrancar, la terminal muestra la dirección. Abre en el navegador:

```
http://localhost:8080
```

Ejecutar las pruebas:

```bash
./mvnw test
```
---
## Arquitectura

```
com.figuras
├── modelo
│   ├── FiguraGeometrica      «interface»  calcularArea(), calcularPerimetro()
│   ├── Figura                «abstract»   nombre, validarMedida()
│   ├── Circulo               radio
│   ├── Rectangulo            base, altura
│   └── Triangulo             ladoA, ladoB, ladoC (fórmula de Herón)
├── servicio
│   ├── FiguraFactory         registro nombre → clase; instancia por reflexión
│   ├── CargadorGroovy        compila código Groovy y lo registra
│   └── ServicioFigurasRemotas consume el catálogo externo (HTTP)
├── controlador
│   ├── FiguraController      /calcular, /cargar
│   ├── CatalogoController    listar, /importar
│   └── ManejadorErrores      @RestControllerAdvice (errores centralizados)
└── MostrarHost               muestra la URL al arrancar
```

**Principios de POO aplicados**

- **Abstracción:** `FiguraGeometrica` define el contrato y `Figura` declara los cálculos abstractos.
- **Herencia:** las figuras nativas y las de Groovy extienden `Figura` y reutilizan `validarMedida()`.
- **Polimorfismo:** el controlador solo llama a `calcularArea()` y `calcularPerimetro()`; no sabe
  qué figura concreta tiene ni si viene de Java o de Groovy. No hay ningún `if`/`switch` por tipo.
- **Encapsulamiento:** las medidas son atributos privados e inmutables.

---

## Crear una figura en Groovy

Cualquier clase que **extienda `Figura`** y tenga un **constructor que reciba `Map`** funciona
como si fuera nativa:

```groovy
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
```

Se puede cargar desde el editor de la página o con `POST /api/figuras/cargar`.
Hay otro ejemplo en [`ejemplos/Rombo.groovy`](ejemplos/Rombo.groovy).

---

## Servicio externo (Extra)

Las figuras adicionales se obtienen del repositorio
[mateo-cuascota/figuras-catalogo](https://github.com/mateo-cuascota/figuras-catalogo).
El catálogo `figuras.json` indica qué figuras hay y dónde está su código:

```json
[
  { "nombre": "Pentagono", "url": "Pentagono.groovy" },
  { "nombre": "Hexagono",  "url": "Hexagono.groovy" },
  { "nombre": "Heptagono", "url": "Heptagono.groovy" }
]
```

Al pulsar **Importar del servicio externo** (o `POST /api/figuras/importar`), la aplicación:

1. Hace un `GET` HTTP al catálogo.
2. Descarga cada archivo `.groovy`.
3. Lo compila con `CargadorGroovy` y lo registra en `FiguraFactory`.

Para agregar una figura nueva basta con subirla al repositorio del catálogo y volver a importar,
sin modificar ni reiniciar esta aplicación.

Configuración en `src/main/resources/application.properties`:

```properties
figuras.remoto.catalogo-url=https://raw.githubusercontent.com/mateo-cuascota/figuras-catalogo/main/figuras.json
figuras.remoto.cargar-al-iniciar=false   # true = importa automáticamente al arrancar
```

---

## Pruebas

14 pruebas unitarias con JUnit (`./mvnw test`):

- **`FiguraFactoryTest`:** instanciación por nombre (Círculo 78.54 / 31.42, Rectángulo 24 / 20,
  Triángulo 6 / 12), solo figuras básicas nativas, medidas inválidas, parámetros faltantes,
  triángulo imposible y figura no registrada.
- **`CargadorGroovyTest`:** el Pentágono en Groovy funciona como nativo y hereda las validaciones;
  rechaza código que no implementa la interfaz, código con errores, nombres duplicados y código vacío.

---

## Limitaciones conocidas

- Las figuras cargadas en Groovy se guardan en memoria y se pierden al reiniciar.
- `/api/figuras/cargar` ejecuta el código Groovy recibido; en un entorno real debería
  restringirse a usuarios de confianza.
