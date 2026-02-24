package TrabajoGrafosCompi;

import java.util.*;

public class Graph {

    //Numero de nodos (serian las aldeas)
    private final int n;

    //Victimas almacenadas por nodo
    private final int[] victimas;

    //Lista de adyacencia: adj.get(u) = lisya de {v, distancia}
    private final List<List<int[]>> adj;

    //Lista plana de todas las arustas para Bellman-Ford
    private final List<int[]> edges; //Serian {u, v, d}

    public Graph(int n) {
        this.n = n;
        this.victimas = new int[n];
        this.adj = new ArrayList<>();
        this.edges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    /*
    u -> es el nodo origen
    v -> es el nodo destino
    d -> es la distancia(peso)
    c -> son las victimas en el nodo v
     */
    public void addEdge(int u, int v, int d, int c) {
        adj.get(u).add(new int[]{v, d});
        edges.add(new int[]{u, v, d});
        //Las victimas se asignan al nodo destino
        victimas[v] = c;
    }

    //Retornamos el número de nodos
    public int getN() {
        return n;
    }

    //Retornamos el número de victimas por nodo
    public int[] getVictimas() {
        return victimas;
    }

    //Retornamos la lista de adyacencia
    public List<List<int[]>> getAdj() {
        return adj;
    }

    //Retornamos la lista plana de aristas {u, v, d}
    public List<int[]> getEdges() {
        return edges;
    }
}
