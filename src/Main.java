
import TrabajoGrafosCompi.BellmanFordMax;
import TrabajoGrafosCompi.DijkstraAlgoritmo;
import TrabajoGrafosCompi.Graph;
import TrabajoGrafosCompi.GraphPanel;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/*
    n = nodos, m = aristas
    u = origen, v = destino, d = distancia, c = víctimas en v
 */
public class Main extends JFrame {

    // Componentes de la GUI
    private JTextArea inputArea;      // Area para ingresar los datos del grafo
    private JTextField startField;    // Nodo de inicio
    private JTextField endField;      // Nodo destino (guarida)
    private JTextArea resultArea;     // Muestra los resultados
    private GraphPanel graphPanel;    // Visualización del grafo
    private JButton runBtn;           // Botón para ejecutar

    // Estado del programa
    private Graph currentGraph;

    // Colores del tema
    private static final Color BG = new Color(15, 10, 30);
    private static final Color PANEL_BG = new Color(25, 18, 45);
    private static final Color TEXT_FG = new Color(220, 200, 255);
    private static final Color ACCENT = new Color(130, 60, 200);
    private static final Color BTN_BG = new Color(80, 30, 140);
    private static final Color BTN_FG = new Color(220, 200, 255);
    private static final Color INPUT_BG = new Color(30, 22, 55);

    /*
      Constructor: construye y muestra la ventana principal.
     */
    public Main() {
        super("🧙 El Ritual de JohlodejVe - Grafos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 750);
        setMinimumSize(new Dimension(1100, 650));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(8, 8));

        buildUI();
        loadExample(); // Carga el ejemplo del PDF al inicio
        setVisible(true);
    }

    /*
      Construye todos los componentes de la interfaz.
     */
    private void buildUI() {
        // ── Panel izquierdo: entrada + resultados ─────────────────────────
        JPanel leftPanel = new JPanel(new BorderLayout(6, 6));
        leftPanel.setBackground(BG);
        leftPanel.setPreferredSize(new Dimension(380, 0));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 5));

        // Título
        JLabel title = new JLabel("🧙 Ritual de JohlodejVe", SwingConstants.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 16));
        title.setForeground(new Color(180, 130, 255));
        title.setBorder(new EmptyBorder(0, 0, 8, 0));
        leftPanel.add(title, BorderLayout.NORTH);

        // Centro: entrada de datos
        JPanel inputPanel = new JPanel(new BorderLayout(4, 4));
        inputPanel.setBackground(PANEL_BG);
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT, 1),
                "Datos del Grafo (n m / u v d c por línea)",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 11), TEXT_FG));

        inputArea = new JTextArea(12, 28);
        inputArea.setBackground(INPUT_BG);
        inputArea.setForeground(TEXT_FG);
        inputArea.setCaretColor(TEXT_FG);
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        inputArea.setBorder(new EmptyBorder(4, 6, 4, 6));
        JScrollPane scrollInput = new JScrollPane(inputArea);
        scrollInput.setBorder(null);
        inputPanel.add(scrollInput, BorderLayout.CENTER);

        // Fila de origen/destino + botón
        JPanel controlRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        controlRow.setBackground(PANEL_BG);

        controlRow.add(styledLabel("Origen:"));
        startField = styledTextField("0", 4);
        controlRow.add(startField);

        controlRow.add(styledLabel("Destino:"));
        endField = styledTextField("6", 4);
        controlRow.add(endField);

        runBtn = new JButton("▶ Ejecutar");
        runBtn.setBackground(BTN_BG);
        runBtn.setForeground(BTN_FG);
        runBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        runBtn.setFocusPainted(false);
        runBtn.setBorder(new EmptyBorder(6, 14, 6, 14));
        runBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        runBtn.addActionListener(e -> runAlgorithms());
        controlRow.add(runBtn);

        inputPanel.add(controlRow, BorderLayout.SOUTH);
        leftPanel.add(inputPanel, BorderLayout.CENTER);

        // Resultados
        JPanel resPanel = new JPanel(new BorderLayout());
        resPanel.setBackground(PANEL_BG);
        resPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT, 1),
                "Resultados",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 11), TEXT_FG));

        resultArea = new JTextArea(12, 28);
        resultArea.setEditable(false);
        resultArea.setBackground(INPUT_BG);
        resultArea.setForeground(new Color(150, 230, 180));
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setBorder(new EmptyBorder(4, 6, 4, 6));
        JScrollPane scrollRes = new JScrollPane(resultArea);
        scrollRes.setBorder(null);
        resPanel.add(scrollRes);
        leftPanel.add(resPanel, BorderLayout.SOUTH);

        // Panel derecho: visualización del grafo
        graphPanel = new GraphPanel();
        graphPanel.setBackground(new Color(15, 10, 30));
        graphPanel.setBorder(BorderFactory.createLineBorder(ACCENT, 1));

        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setBackground(BG);
        rightWrapper.setBorder(new EmptyBorder(10, 5, 10, 10));
        rightWrapper.add(graphPanel, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);
        add(rightWrapper, BorderLayout.CENTER);
    }

    /*
      Crea una etiqueta con el estilo del tema.
     */
    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_FG);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        return l;
    }

    /*
      Crea un campo de texto estilizado.
     */
    private JTextField styledTextField(String def, int cols) {
        JTextField tf = new JTextField(def, cols);
        tf.setBackground(INPUT_BG);
        tf.setForeground(TEXT_FG);
        tf.setCaretColor(TEXT_FG);
        tf.setFont(new Font("Monospaced", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT),
                new EmptyBorder(2, 4, 2, 4)));
        return tf;
    }

    /*
      Carga el ejemplo del PDF en el área de entrada.
     */
    private void loadExample() {
        inputArea.setText(
                "7 8\n" +
                        "0 1 4 3\n" +
                        "0 2 2 0\n" +
                        "1 3 5 5\n" +
                        "2 3 8 5\n" +
                        "2 4 10 1\n" +
                        "3 5 2 4\n" +
                        "5 6 3 0\n" +
                        "4 5 2 4"
        );
        startField.setText("0");
        endField.setText("6");
    }

    /*
      Lee los datos, construye el grafo, ejecuta los algoritmos y muestra resultados.
     */
    private void runAlgorithms() {
        try {
            // 1. Parsear entrada
            String rawInput = inputArea.getText().trim();
            Scanner sc = new Scanner(rawInput);

            int n = sc.nextInt(); // número de nodos
            int m = sc.nextInt(); // número de aristas

            Graph graph = new Graph(n);

            for (int i = 0; i < m; i++) {
                int u = sc.nextInt();
                int v = sc.nextInt();
                int d = sc.nextInt();
                int c = sc.nextInt();
                graph.addEdge(u, v, d, c);
            }

            int start = Integer.parseInt(startField.getText().trim());
            int end = Integer.parseInt(endField.getText().trim());

            if (start < 0 || start >= n || end < 0 || end >= n) {
                showError("Origen o destino fuera de rango (0 a " + (n - 1) + ")");
                return;
            }

            currentGraph = graph;

            // 2. Dijkstra
            DijkstraAlgoritmo dij = new DijkstraAlgoritmo(graph);
            dij.run(start);
            List<Integer> dijPath = dij.getPath(end);
            int dijDist = dij.getDistance(end);
            int dijVictims = dij.countVictims(dijPath);

            // 3. Bellman-Ford adaptado
            BellmanFordMax bf = new BellmanFordMax(graph);
            bf.run(start);
            List<Integer> bfPath = bf.getPath(end);
            int bfVictims = bf.getMaxVictima(end);
            int bfDist = bf.calcDistancia(bfPath);

            // 4. Actualizar GUI
            graphPanel.setGraph(graph, start, end);
            graphPanel.setPaths(dijPath, bfPath);

            // 5. Mostrar resultados
            StringBuilder sb = new StringBuilder();
            sb.append("=== RESULTADOS DEL RITUAL ===\n\n");

            sb.append("1. CAMINO MÁS CORTO (DIJKSTRA)\n");
            sb.append("────────────────────────────\n");
            if (dijPath.isEmpty()) {
                sb.append("  ❌ No existe camino\n");
            } else {
                sb.append("  Nodos    : ").append(dijPath).append("\n");
                sb.append("  Distancia: ").append(dijDist).append("\n");
                sb.append("  Víctimas : ").append(dijVictims).append("\n");
            }

            sb.append("\n2. MÁS VÍCTIMAS (BELLMAN-FORD)\n");
            sb.append("────────────────────────────\n");
            if (bfPath.isEmpty()) {
                sb.append("  ❌ No existe camino\n");
            } else {
                sb.append("  Nodos    : ").append(bfPath).append("\n");
                sb.append("  Distancia: ").append(bfDist).append("\n");
                sb.append("  Víctimas : ").append(bfVictims).append("\n");
            }

            sb.append("\n════════════════════════════\n");
            sb.append(" TABLA RESUMEN\n");
            sb.append("════════════════════════════\n");
            sb.append(String.format("%-12s | %-20s | %s | %s%n",
                    "Algoritmo", "Camino", "Dist", "Víct"));
            sb.append("─────────────────────────────────────────\n");
            sb.append(String.format("%-12s | %-20s | %-4d | %d%n",
                    "Dijkstra", dijPath.toString(), dijDist, dijVictims));
            sb.append(String.format("%-12s | %-20s | %-4d | %d%n",
                    "Bellman-Ford", bfPath.toString(), bfDist, bfVictims));

            resultArea.setText(sb.toString());
            resultArea.setCaretPosition(0);

        } catch (Exception ex) {
            showError("Error al procesar la entrada:\n" + ex.getMessage() +
                    "\n\nFormato esperado:\nn m\nu v d c\n...");
        }
    }

    /*
      Muestra un diálogo de error.
     */
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error de entrada",
                JOptionPane.ERROR_MESSAGE);
    }

    /*
      Punto de entrada del programa.
     */
    public static void main(String[] args) {
        // Usar el look and feel del sistema si está disponible
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Ejecutar en el hilo de eventos de Swing
        SwingUtilities.invokeLater(Main::new);
    }
}
