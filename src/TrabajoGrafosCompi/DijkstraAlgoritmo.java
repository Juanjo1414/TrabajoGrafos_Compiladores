package TrabajoGrafosCompi;

import java.util.*;

/*
 Implementación de Dijkstra para encontrar el camino más corto
 (menor distancia total) desde un nodo origen hasta un destino.
 */
public class DijkstraAlgoritmo {

    private final Graph graph;

    //Distancias mínimas desde el origen

    private int[] dist;


     //Padre de cada nodo en el camino más corto

    private int[] parent;

    public DijkstraAlgoritmo(Graph graph) {
        this.graph = graph;
    }

    public void run(int start) {
        int n = graph.getN();
        dist = new int[n];
        parent = new int[n];

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dist[start] = 0;

        // Cola de prioridad: {distancia, nodo}
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.add(new int[]{0, start});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int d = curr[0];
            int u = curr[1];

            // Si ya encontramos una distancia menor, saltamos
            if (d > dist[u]) continue;

            for (int[] edge : graph.getAdj().get(u)) {
                int v = edge[0];
                int w = edge[1];
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    parent[v] = u;
                    pq.add(new int[]{dist[v], v});
                }
            }
        }
    }

    public List<Integer> getPath(int end) {
        List<Integer> path = new ArrayList<>();
        if (dist[end] == Integer.MAX_VALUE) return path; // Sin camino

        // Reconstruir desde el destino hacia el origen
        for (int cur = end; cur != -1; cur = parent[cur]) {
            path.add(cur);
        }
        Collections.reverse(path);
        return path;
    }

    public int getDistance(int end) {
        return dist[end] == Integer.MAX_VALUE ? -1 : dist[end];
    }

    public int countVictims(List<Integer> path) {
        int total = 0;
        int[] victims = graph.getVictimas();
        Set<Integer> visited = new HashSet<>();
        for (int node : path) {
            if (visited.add(node)) {
                total += victims[node];
            }
        }
        return total;
    }
}
