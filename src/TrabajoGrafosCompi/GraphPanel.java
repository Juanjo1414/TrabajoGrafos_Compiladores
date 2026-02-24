package TrabajoGrafosCompi;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * Panel Swing que visualiza el grafo de aldeas.
 * Dibuja nodos, aristas con pesos, y resalta los caminos calculados.
 */
public class GraphPanel extends JPanel {

    /**
     * Radio de cada nodo dibujado
     */
    private static final int NODE_RADIUS = 28;

    /**
     * Margen del panel
     */
    private static final int MARGIN = 60;

    /**
     * Grafo a visualizar
     */
    private Graph graph;

    /**
     * Posiciones (x, y) de cada nodo en el panel
     */
    private int[][] positions;

    /**
     * Camino más corto (Dijkstra) a resaltar
     */
    private List<Integer> shortestPath;

    /**
     * Camino de más víctimas (Bellman-Ford) a resaltar
     */
    private List<Integer> maxVictimsPath;

    /**
     * Nodo origen seleccionado
     */
    private int start;

    /**
     * Nodo destino seleccionado
     */
    private int end;

    // Colores del tema oscuro/brujo
    private static final Color BG_COLOR = new Color(15, 10, 30);
    private static final Color NODE_COLOR = new Color(60, 30, 90);
    private static final Color NODE_BORDER = new Color(150, 80, 220);
    private static final Color EDGE_COLOR = new Color(80, 60, 120);
    private static final Color DIJKSTRA_COLOR = new Color(0, 220, 150);
    private static final Color BELLMAN_COLOR = new Color(255, 80, 80);
    private static final Color TEXT_COLOR = new Color(220, 200, 255);
    private static final Color WEIGHT_COLOR = new Color(180, 160, 220);
    private static final Color START_COLOR = new Color(50, 200, 50);
    private static final Color END_COLOR = new Color(255, 50, 50);
    private static final Color VICTIM_COLOR = new Color(255, 150, 50);

    /**
     * Inicializa el panel con fondo oscuro.
     */
    public GraphPanel() {
        setBackground(BG_COLOR);
        setPreferredSize(new Dimension(800, 600));
        shortestPath = new ArrayList<>();
        maxVictimsPath = new ArrayList<>();
    }

    /**
     * Establece el grafo y calcula las posiciones de los nodos en círculo.
     *
     * @param graph grafo a mostrar
     * @param start nodo de inicio
     * @param end   nodo destino
     */
    public void setGraph(Graph graph, int start, int end) {
        this.graph = graph;
        this.start = start;
        this.end = end;
        this.shortestPath = new ArrayList<>();
        this.maxVictimsPath = new ArrayList<>();
        calculatePositions();
        repaint();
    }

    /**
     * Establece los caminos a resaltar y repinta.
     *
     * @param shortest   camino más corto
     * @param maxVictims camino de más víctimas
     */
    public void setPaths(List<Integer> shortest, List<Integer> maxVictims) {
        this.shortestPath = shortest != null ? shortest : new ArrayList<>();
        this.maxVictimsPath = maxVictims != null ? maxVictims : new ArrayList<>();
        repaint();
    }

    /**
     * Calcula las posiciones de los nodos distribuidos en un círculo.
     */
    private void calculatePositions() {
        int n = graph.getN();
        positions = new int[n][2];
        int w = getWidth() > 0 ? getWidth() : 800;
        int h = getHeight() > 0 ? getHeight() : 600;
        int cx = w / 2;
        int cy = h / 2;
        int r = Math.min(w, h) / 2 - MARGIN - NODE_RADIUS;

        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n - Math.PI / 2;
            positions[i][0] = (int) (cx + r * Math.cos(angle));
            positions[i][1] = (int) (cy + r * Math.sin(angle));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graph == null) return;

        // Recalcular posiciones si el panel cambió de tamaño
        calculatePositions();

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Dibujar título
        g2.setColor(new Color(180, 130, 255));
        g2.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2.drawString("🧙 El Ritual de JohlodejVe - Grafo de Aldeas", 15, 20);

        // Dibujar leyenda
        drawLegend(g2);

        // Dibujar aristas
        drawEdges(g2);

        // Dibujar nodos encima de las aristas
        drawNodes(g2);
    }

    /**
     * Dibuja todas las aristas del grafo, resaltando los caminos.
     */
    private void drawEdges(Graphics2D g2) {
        List<List<int[]>> adj = graph.getAdj();
        int n = graph.getN();

        for (int u = 0; u < n; u++) {
            for (int[] edge : adj.get(u)) {
                int v = edge[0];
                int d = edge[1];

                boolean inShortest = isEdgeInPath(u, v, shortestPath);
                boolean inMaxVic = isEdgeInPath(u, v, maxVictimsPath);

                // Elegir color y grosor según si pertenece a algún camino
                if (inShortest && inMaxVic) {
                    // Ambos caminos comparten esta arista: mostrar Dijkstra
                    g2.setColor(DIJKSTRA_COLOR);
                    g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                } else if (inShortest) {
                    g2.setColor(DIJKSTRA_COLOR);
                    g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                } else if (inMaxVic) {
                    g2.setColor(BELLMAN_COLOR);
                    g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                } else {
                    g2.setColor(EDGE_COLOR);
                    g2.setStroke(new BasicStroke(1.5f));
                }

                drawArrow(g2, positions[u][0], positions[u][1],
                        positions[v][0], positions[v][1]);

                // Etiqueta del peso en el punto medio
                int mx = (positions[u][0] + positions[v][0]) / 2;
                int my = (positions[u][1] + positions[v][1]) / 2;
                g2.setColor(WEIGHT_COLOR);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                g2.drawString("d:" + d, mx + 4, my - 4);
            }
        }
    }

    /**
     * Dibuja una flecha dirigida entre dos puntos.
     */
    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len == 0) return;

        // Acortar la línea para que no tape los nodos
        double ux = dx / len;
        double uy = dy / len;
        int sx = (int) (x1 + ux * NODE_RADIUS);
        int sy = (int) (y1 + uy * NODE_RADIUS);
        int ex = (int) (x2 - ux * NODE_RADIUS);
        int ey = (int) (y2 - uy * NODE_RADIUS);

        g2.drawLine(sx, sy, ex, ey);

        // Punta de flecha
        double arrowLen = 10;
        double arrowAngle = Math.toRadians(25);
        double angle = Math.atan2(dy, dx);

        int ax1 = (int) (ex - arrowLen * Math.cos(angle - arrowAngle));
        int ay1 = (int) (ey - arrowLen * Math.sin(angle - arrowAngle));
        int ax2 = (int) (ex - arrowLen * Math.cos(angle + arrowAngle));
        int ay2 = (int) (ey - arrowLen * Math.sin(angle + arrowAngle));

        g2.fillPolygon(new int[]{ex, ax1, ax2}, new int[]{ey, ay1, ay2}, 3);
    }

    /**
     * Dibuja todos los nodos del grafo.
     */
    private void drawNodes(Graphics2D g2) {
        int[] victims = graph.getVictimas();
        for (int i = 0; i < graph.getN(); i++) {
            int x = positions[i][0];
            int y = positions[i][1];

            // Color especial para inicio y fin
            Color fill;
            if (i == start) {
                fill = START_COLOR;
            } else if (i == end) {
                fill = END_COLOR;
            } else {
                fill = NODE_COLOR;
            }

            // Sombra/glow si está en algún camino
            boolean inS = shortestPath.contains(i);
            boolean inM = maxVictimsPath.contains(i);
            if (inS || inM) {
                Color glow = inS && inM ? DIJKSTRA_COLOR : inS ? DIJKSTRA_COLOR : BELLMAN_COLOR;
                g2.setColor(new Color(glow.getRed(), glow.getGreen(), glow.getBlue(), 60));
                g2.fillOval(x - NODE_RADIUS - 6, y - NODE_RADIUS - 6,
                        (NODE_RADIUS + 6) * 2, (NODE_RADIUS + 6) * 2);
            }

            // Círculo relleno
            g2.setColor(fill);
            g2.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

            // Borde
            g2.setColor(NODE_BORDER);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

            // Número de nodo
            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("Monospaced", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            String label = String.valueOf(i);
            g2.drawString(label, x - fm.stringWidth(label) / 2, y + 5);

            // Víctimas debajo del nodo
            if (victims[i] > 0) {
                g2.setColor(VICTIM_COLOR);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                String vLabel = "👻" + victims[i];
                g2.drawString(vLabel, x - fm.stringWidth(vLabel) / 2 - 2, y + NODE_RADIUS + 13);
            }
        }
    }

    /**
     * Dibuja la leyenda en la esquina inferior izquierda.
     */
    private void drawLegend(Graphics2D g2) {
        int lx = 15, ly = getHeight() - 80;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));

        g2.setColor(DIJKSTRA_COLOR);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(lx, ly, lx + 25, ly);
        g2.setColor(TEXT_COLOR);
        g2.drawString("Dijkstra (más corto)", lx + 30, ly + 4);

        ly += 18;
        g2.setColor(BELLMAN_COLOR);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(lx, ly, lx + 25, ly);
        g2.setColor(TEXT_COLOR);
        g2.drawString("Bellman-Ford (más víctimas)", lx + 30, ly + 4);

        ly += 18;
        g2.setColor(START_COLOR);
        g2.fillOval(lx, ly - 8, 12, 12);
        g2.setColor(TEXT_COLOR);
        g2.drawString("Origen  ", lx + 18, ly + 4);

        g2.setColor(END_COLOR);
        g2.fillOval(lx + 65, ly - 8, 12, 12);
        g2.drawString("Destino (guarida)", lx + 83, ly + 4);
    }

    /**
     * Verifica si la arista (u→v) está en el camino dado.
     */
    private boolean isEdgeInPath(int u, int v, List<Integer> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            if (path.get(i) == u && path.get(i + 1) == v) return true;
        }
        return false;
    }
}
