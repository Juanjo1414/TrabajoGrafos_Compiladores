package TrabajoGrafosCompi;

import java.util.*;

public class BellmanFordMax {
    private final Graph graph;

    //Máximas victimas acumuladas llegando a cada nodo
    private int[] maxVictimas;

    //Padre de cada nodo en el camino de más victimas
    private int[] padre;

    public BellmanFordMax(Graph graph) {
        this.graph = graph;
    }

    public void run(int inicio) {
        int n = graph.getN();
        maxVictimas = new int[n];
        padre = new int[n];

        Arrays.fill(maxVictimas, -1); //-1 es nodo no alcanzado
        Arrays.fill(padre, -1);

        // El nodo inicial tiene las víctimas del nodo 0 si inicio==0
        // Según el problema, las víctimas están en el nodo v (destino de arista)
        // El origen no aporta víctimas inicialmente
        maxVictimas[inicio] = 0;

        //n-1 veces las aristas (Se aplica Bellman-Ford estandar pero maximizado
        for (int i = 1; i < n; i++) {
            boolean updated = false;
            for (int[] edge : graph.getEdges()) {
                int u = edge[0];
                int v = edge[1];
                //Solo procesamos si u es alcanzable
                if (maxVictimas[u] < 0) continue;

                //Victimas del nodo v (solo si no fue visitado en este camino)
                //Aproximación: sumamos victimas de v si el padre no es el mismo nodo
                int victimasV = graph.getVictimas()[v];

                //Calculamos las victimas acumuladas llegando a v por este camino
                int nuevasVictimas = maxVictimas[u] + victimasV;

                if (nuevasVictimas > maxVictimas[v]) {
                    maxVictimas[v] = nuevasVictimas;
                    padre[v] = u;
                    updated = true;
                }
            }
            //Optimización: si no hubo actualizaciones, terminamos
            if (!updated) break;

        }
    }

    public List<Integer> getPath(int fin) {
        List<Integer> path = new ArrayList<>();
        if (maxVictimas[fin] < 0) return path; //Sin camino

        // Seguimos el arreglo padre[] hacia atrás desde el destino
        for (int cur = fin; cur != -1; cur = padre[cur]) {
            path.add(cur);
            //Seguridad: evitar ciclos infinitos
            if (path.size() > graph.getN()) break;
        }
        Collections.reverse(path); // estaba al revés, lo volteamos
        return path;
    }

    //fin es el nodo destino
    //Máximas victimas recolectadas llegando a fin, o -1 si no es alcanzable
    public int getMaxVictima(int fin) {
        return maxVictimas[fin];
    }

    //Calculamos la distancia total de un camino dado
    public int calcDistancia(List<Integer> path) {
        if (path.size() < 2) return 0;
        int total = 0;
        List<List<int[]>> adj = graph.getAdj();
        for (int i = 0; i < path.size() - 1; i++) {
            int u = path.get(i);
            int v = path.get(i + 1);
            for (int[] edge : adj.get(u)) {
                if (edge[0] == v) {
                    total += edge[1];
                    break;
                }
            }
        }
        return total;
    }
}
