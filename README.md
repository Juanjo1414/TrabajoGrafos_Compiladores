# 🧙‍♂️ El Ritual de JohlodejVe

> **Trabajo de Grafos — Estructuras de Datos y Algoritmos**  
> Aplicación Java con interfaz gráfica para resolver problemas de caminos en grafos dirigidos y ponderados.

---

## 📖 Descripción del Problema

JohlodejVe es un brujo inmortal que cada luna llena debe cazar víctimas y regresar a su guarida antes del amanecer. Dado un mapa de aldeas modelado como un grafo, el programa resuelve dos objetivos:

| # | Objetivo | Algoritmo |
|---|----------|-----------|
| 1 | **Ruta de Escape** — camino con menor distancia total desde el origen hasta la guarida | Dijkstra |
| 2 | **Ruta de Cacería** — camino que maximice las víctimas recolectadas | Bellman-Ford Adaptado |

> ⚠️ Las víctimas de un nodo **solo se cuentan la primera vez** que se visita.

---

## 🗂️ Estructura del Proyecto

```
JohlodejVe/
├── src/
│   └── TrabajoGrafosCompi/
│       ├── Graph.java               ← Modelo del grafo (lista de adyacencia)
│       ├── DijkstraAlgorithm.java   ← Algoritmo de Dijkstra
│       ├── BellmanFordMax.java      ← Bellman-Ford adaptado (maximizar víctimas)
│       ├── GraphPanel.java          ← Visualización gráfica con Java Swing
│       └── MainApp.java             ← Ventana principal y punto de entrada
├              
└── README.md
```

---

## ⚙️ Requisitos

- **Java JDK 11 o superior** (probado con JDK 21)
- No requiere dependencias externas — usa únicamente la librería estándar de Java (`javax.swing`, `java.util`)

Verificar instalación:
```bash
java -version
javac -version
```
---

## 📋 Formato de Entrada

Los datos **se ingresan desde la interfaz gráfica** (no están hardcodeados). El formato es:

```
n m
u₁ v₁ d₁ c₁
u₂ v₂ d₂ c₂
...
```

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `n` | entero | Número total de nodos (aldeas), numerados desde `0` |
| `m` | entero | Número total de aristas (caminos) |
| `u` | entero | Nodo **origen** de la arista |
| `v` | entero | Nodo **destino** de la arista |
| `d` | entero | **Distancia** (peso) del camino entre `u` y `v` |
| `c` | entero | **Víctimas** ubicadas en el nodo `v` |

> El grafo es **dirigido**: una arista `u → v` no implica que exista `v → u`.

---

## 🧪 Ejemplo de Prueba

### Entrada

```
7 8
0 1 4 3
0 2 2 0
1 3 5 5
2 3 8 5
2 4 10 1
3 5 2 4
5 6 3 0
4 5 2 4
```

### Representación del grafo

```
    (3)       (5)       (4)       (0)
 0 ──4──► 1 ──5──► 3 ──2──► 5 ──3──► 6
 │                 ▲         ▲
 └──2──► 2 ──8─────┘         │
    (0)   └──10──► 4 ──2─────┘
               (1)       (4)
```

*(el número entre paréntesis es la cantidad de víctimas en cada nodo)*

### Salida Esperada

```
=== RESULTADOS DEL RITUAL ===

1. CAMINO MÁS CORTO (DIJKSTRA)
────────────────────────────
  Nodos    : [0, 1, 3, 5, 6]
  Distancia: 14
  Víctimas : 12

2. MÁS VÍCTIMAS (BELLMAN-FORD)
────────────────────────────
  Nodos    : [0, 1, 3, 5, 6]
  Distancia: 14
  Víctimas : 12

════════════════════════════
 TABLA RESUMEN
════════════════════════════
Algoritmo    | Camino              | Dist | Víct
─────────────────────────────────────────────────
Dijkstra     | [0, 1, 3, 5, 6]    | 14   | 12
Bellman-Ford | [0, 1, 3, 5, 6]    | 14   | 12
```

---

## 🖥️ Interfaz Gráfica

La ventana se divide en dos paneles:

### Panel Izquierdo — Control
- **Área de texto**: ingresar los datos del grafo en el formato especificado
- **Campos Origen / Destino**: nodo de inicio y nodo de la guarida
- **Botón ▶ Ejecutar**: corre ambos algoritmos y actualiza la visualización
- **Panel de resultados**: muestra caminos, distancias y víctimas de cada algoritmo

### Panel Derecho — Visualización
- Nodos distribuidos en **círculo**, numerados desde `0`
- **Flechas dirigidas** con el peso de la distancia (`d:`)
- **👻** bajo cada nodo indica la cantidad de víctimas
- Colores de los caminos resaltados:

| Color | Significado |
|-------|-------------|
| 🟢 Verde | Camino Dijkstra (más corto) |
| 🔴 Rojo | Camino Bellman-Ford (más víctimas) |
| 🟢 Nodo verde | Nodo origen |
| 🔴 Nodo rojo | Nodo destino (guarida) |

---

## 🧠 Algoritmos Implementados

### Dijkstra — Camino más corto

**Idea:** Siempre procesar el nodo más cercano al origen que aún no fue visitado.

**Estructura de datos clave:** `PriorityQueue<int[]>` (min-heap por distancia)

**Flujo:**
1. Inicializar todas las distancias en `∞`, excepto el origen en `0`
2. Insertar el origen en la cola de prioridad
3. Mientras la cola no esté vacía:
   - Extraer el nodo `u` con menor distancia
   - Para cada vecino `v` de `u`: si `dist[u] + peso(u,v) < dist[v]`, actualizar
4. Reconstruir el camino siguiendo el arreglo `parent[]` desde el destino

**Complejidad:** `O((V + E) log V)`

**¿Por qué no sirve para maximizar víctimas?**
Dijkstra garantiza el óptimo solo cuando se **minimiza** una función monótona creciente con pesos positivos. Maximizar no cumple esa propiedad.

---

### Bellman-Ford Adaptado — Máximas víctimas

**Idea:** Relajar todas las aristas `n-1` veces, pero en vez de minimizar distancia, **maximizar víctimas acumuladas**.

**Adaptación clave:**

| Bellman-Ford clásico | Versión adaptada |
|----------------------|-----------------|
| `dist[v] = ∞` inicial | `maxVict[v] = -1` inicial |
| Condición: `dist[u] + w < dist[v]` | Condición: `maxVict[u] + c > maxVict[v]` |
| Objetivo: minimizar distancia | Objetivo: maximizar víctimas |

**¿Por qué n-1 iteraciones?**
En un grafo de `n` nodos, el camino simple más largo posible tiene `n-1` aristas. Después de `n-1` relajaciones todos los caminos posibles ya fueron considerados.

**Complejidad:** `O(V × E)`

---

## 📐 Decisiones de Diseño

### ¿Por qué lista de adyacencia y no matriz?

| Estructura | Memoria | Acceso vecinos | Ideal para |
|------------|---------|----------------|------------|
| Matriz de adyacencia | `O(V²)` | `O(1)` | Grafos densos |
| Lista de adyacencia | `O(V + E)` | `O(grado)` | Grafos dispersos ✅ |

En grafos de redes de caminos (como aldeas), el número de aristas `E` suele ser mucho menor que `V²`, por lo que la lista de adyacencia es más eficiente.

### ¿Por qué arreglo `parent[]` para reconstruir el camino?

Durante la ejecución de cada algoritmo, cada vez que encontramos un camino mejor hacia un nodo `v` llegando desde `u`, guardamos `parent[v] = u`. Al finalizar, solo hay que seguir el rastro desde el destino hasta el origen y luego invertirlo.

---

## 👥 Integrantes del Grupo

| Nombre | Rol |
|--------|-----|
| _(Juan Jose Jaramillo Mora)_ | Algoritmos (Dijkstra + Bellman-Ford) |
| _(Dylan Mejia)_ | Modelo del grafo + pruebas e Interfaz gráfica (GUI) |

---

## 📚 Referencias

- Cormen, T. H. et al. *Introduction to Algorithms* (CLRS), 4th ed. — Capítulos 22 y 24
- Sedgewick, R. *Algorithms in Java* — Part 5: Graph Algorithms
- Documentación oficial de Java SE: [docs.oracle.com](https://docs.oracle.com/en/java/)
