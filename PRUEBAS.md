# Pruebas de la API de figuras

Arranca la app (F5 en FigurasSpringApplication o `.\mvnw spring-boot:run`)
y ejecuta en PowerShell (usa `curl.exe`, no el alias `curl`).

## 1. Figuras nativas (instancia por nombre de clase + parámetros)
```powershell
curl.exe -X POST http://localhost:8080/api/figuras/calcular -H "Content-Type: application/json" -d '{\"clase\":\"Circulo\",\"parametros\":{\"radio\":5}}'
curl.exe -X POST http://localhost:8080/api/figuras/calcular -H "Content-Type: application/json" -d '{\"clase\":\"Rectangulo\",\"parametros\":{\"base\":4,\"altura\":6}}'
curl.exe -X POST http://localhost:8080/api/figuras/calcular -H "Content-Type: application/json" -d '{\"clase\":\"Triangulo\",\"parametros\":{\"ladoA\":3,\"ladoB\":4,\"ladoC\":5}}'
```
Esperado: Circulo 78.54 / 31.42 · Rectangulo 24 / 20 · Triangulo 6 / 12

## 2. Ver figuras registradas
```powershell
curl.exe http://localhost:8080/api/figuras
```

## 3. Cargar una figura Groovy en tiempo de ejecución
```powershell
curl.exe -X POST http://localhost:8080/api/figuras/cargar -H "Content-Type: text/plain" --data-binary "@ejemplos/Rombo.groovy"
curl.exe -X POST http://localhost:8080/api/figuras/calcular -H "Content-Type: application/json" -d '{\"clase\":\"Rombo\",\"parametros\":{\"diagonalMayor\":6,\"diagonalMenor\":8}}'
```
Esperado: Rombo 24 / 20

## 4. Extra: importar figuras desde el servicio externo
Con `figuras.remoto.cargar-al-iniciar=true` se importan solas al arrancar.
También se puede forzar:
```powershell
curl.exe -X POST http://localhost:8080/api/figuras/importar
curl.exe -X POST http://localhost:8080/api/figuras/calcular -H "Content-Type: application/json" -d '{\"clase\":\"Pentagono\",\"parametros\":{\"lado\":4}}'
curl.exe -X POST http://localhost:8080/api/figuras/calcular -H "Content-Type: application/json" -d '{\"clase\":\"Hexagono\",\"parametros\":{\"lado\":2}}'
```
Esperado: Pentagono 27.53 / 20 · Hexagono 10.39 / 12

## Cómo funciona el Extra
`ServicioFigurasRemotas` hace GET a `figuras.remoto.catalogo-url`, que
devuelve un JSON como:
```json
[ { "nombre": "Pentagono", "url": "Pentagono.groovy" } ]
```
Descarga cada `.groovy`, lo compila con `CargadorGroovy` y lo registra en
`FiguraFactory`, igual que una clase nativa. El catálogo de ejemplo está en
`src/main/resources/static/catalogo/`; para un servicio externo real sube
esos archivos a GitHub (o cualquier servidor) y cambia la URL.

Ojo: cargar código desde una URL ejecuta ese código en tu servidor. Usa
solo fuentes de confianza.
