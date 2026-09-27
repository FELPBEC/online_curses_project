package co.edu.uptc.viewController;

import co.edu.uptc.interfaces.EducativeElement;
import co.edu.uptc.interfaces.EducativeElementType;
import co.edu.uptc.model.Lessons;
import co.edu.uptc.model.TreeNode;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Componente gráfico reutilizable para el renderizado e interacción visual 
 * de árboles N-arios de cursos (Cursos, Módulos y Lecciones) en JavaFX.
 * Diseñado bajo el principio de responsabilidad única: únicamente renderiza
 * la estructura visual, gestiona Tooltips e informa selecciones de nodos.
 * 
 * 
 * @author @jm1407db
 * @version v1.0
 */
public class CourseTreeView extends ScrollPane {

    private static final double NODE_RADIUS = 26;
    private static final double HORIZONTAL_GAP = 120;
    private static final double VERTICAL_GAP = 95;
    private static final double MARGIN = 70;

    private final Pane canvasPane;
    private TreeNode<EducativeElement> currentRoot;
    private TreeNode<EducativeElement> selectedNode;

    private Consumer<TreeNode<EducativeElement>> onNodeSelectedListener;
    private final Map<TreeNode<EducativeElement>, Point> positions = new HashMap<>();

    /**
     * Constructor que inicializa el lienzo dentro del panel de desplazamiento (ScrollPane).
     */
    public CourseTreeView() {
        this.canvasPane = new Pane();
        this.canvasPane.setMinSize(800, 500);

        // Permite deseleccionar al hacer clic en el fondo del lienzo
        this.canvasPane.setOnMouseClicked(event -> {
            if (event.getTarget() == canvasPane) {
                clearSelection();
            }
        });

        setContent(canvasPane);
        setPannable(true); // Permite arrastrar el lienzo con el ratón
        setFitToWidth(true);
        setFitToHeight(true);
        setStyle("-fx-background-color: transparent; -fx-background: #ffffff;");
    }

    /**
     * Renderiza el árbol N-ario completo en el lienzo a partir del nodo raíz.
     * 
     * @param root Nodo raíz del curso a dibujar.
     */
    public void render(TreeNode<EducativeElement> root) {
        this.currentRoot = root;
        canvasPane.getChildren().clear();
        positions.clear();

        if (root == null || root.getData() == null) {
            Label label = new Label("No hay información de árbol para mostrar.");
            label.setStyle("-fx-font-size: 14px; -fx-text-fill: #777777;");
            label.setLayoutX(30);
            label.setLayoutY(30);
            canvasPane.getChildren().add(label);
            return;
        }

        calculatePositions(root, 0, new int[]{0});
        drawEdges(root);
        drawNodes(root);
        adjustCanvasSize();
    }

    /**
     * Define el callback que se ejecutará cuando un usuario (Estudiante o Admin) 
     * haga clic sobre un nodo del árbol.
     * 
     * @param listener Expresión lambda o consumidor con el nodo seleccionado.
     */
    public void setOnNodeSelectedListener(Consumer<TreeNode<EducativeElement>> listener) {
        this.onNodeSelectedListener = listener;
    }

    /**
     * Obtiene el nodo que se encuentra actualmente seleccionado en el árbol.
     * 
     * @return {@link TreeNode} seleccionado o {@code null} si no hay selección active.
     */
    public TreeNode<EducativeElement> getSelectedNode() {
        return selectedNode;
    }

    /**
     * Cancela la selección visual actual y notifica a los escuchadores.
     */
    public void clearSelection() {
        this.selectedNode = null;
        if (onNodeSelectedListener != null) {
            onNodeSelectedListener.accept(null);
        }
        if (currentRoot != null) {
            render(currentRoot);
        }
    }

    /**
     * Algoritmo de cálculo de coordenadas en espacio 2D para cada nodo N-ario.
     */
    private void calculatePositions(TreeNode<EducativeElement> node, int depth, int[] index) {
        if (node == null) return;

        List<TreeNode<EducativeElement>> sons = node.getSons();

        if (sons == null || sons.isEmpty()) {
            double x = MARGIN + index[0] * HORIZONTAL_GAP;
            double y = MARGIN + depth * VERTICAL_GAP;
            positions.put(node, new Point(x, y));
            index[0]++;
        } else {
            for (TreeNode<EducativeElement> son : sons) {
                calculatePositions(son, depth + 1, index);
            }
            double firstChildX = positions.get(sons.get(0)).x;
            double lastChildX = positions.get(sons.get(sons.size() - 1)).x;

            double x = (firstChildX + lastChildX) / 2.0;
            double y = MARGIN + depth * VERTICAL_GAP;
            positions.put(node, new Point(x, y));
        }
    }

    /**
     * Dibuja las líneas de unión entre nodos padres e hijos.
     */
    private void drawEdges(TreeNode<EducativeElement> node) {
        if (node == null || node.getSons() == null) return;

        Point parentPos = positions.get(node);

        for (TreeNode<EducativeElement> son : node.getSons()) {
            Point childPos = positions.get(son);
            if (childPos != null) {
                Line line = new Line(parentPos.x, parentPos.y, childPos.x, childPos.y);
                line.setStroke(Color.DARKGRAY);
                line.setStrokeWidth(2);
                canvasPane.getChildren().add(line);

                drawEdges(son);
            }
        }
    }

    /**
     * Renderiza las figuras geométricas, textos y configura eventos y tooltips por nodo.
     */
    private void drawNodes(TreeNode<EducativeElement> node) {
        if (node == null || node.getData() == null) return;

        Point position = positions.get(node);
        EducativeElement element = node.getData();

        Circle circle = new Circle(position.x, position.y, NODE_RADIUS);

        // Estilo de selección
        if (node.equals(selectedNode)) {
            circle.setStroke(Color.RED);
            circle.setStrokeWidth(3.5);
        } else {
            circle.setStroke(Color.DARKSLATEGRAY);
            circle.setStrokeWidth(1.5);
        }

        // Color por tipo de elemento
        if (element.getElementType() == EducativeElementType.COURSE) {
            circle.setFill(Color.LIGHTBLUE);
        } else if (element.getElementType() == EducativeElementType.MODULO) {
            circle.setFill(Color.LIGHTGREEN);
        } else {
            circle.setFill(Color.LIGHTYELLOW);
        }

        // Asignación de Tooltip con información detallada
        Tooltip tooltip = createNodeTooltip(element);
        Tooltip.install(circle, tooltip);

        // Evento de selección
        circle.setOnMouseClicked(event -> {
            event.consume();
            if (node.equals(selectedNode)) {
                clearSelection();
            } else {
                this.selectedNode = node;
                render(currentRoot);
                if (onNodeSelectedListener != null) {
                    onNodeSelectedListener.accept(selectedNode);
                }
            }
        });

        // Título abreviado en el centro del nodo
        String displayTitle = element.getTitle() != null && !element.getTitle().isBlank() 
                ? element.getTitle() 
                : element.getId();

        Text centerText = new Text(truncateText(displayTitle, 8));
        centerText.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
        centerText.setX(position.x - centerText.getLayoutBounds().getWidth() / 2);
        centerText.setY(position.y + 4);
        centerText.setMouseTransparent(true);

        // Tipo de nodo impreso debajo del círculo
        Text typeText = new Text(element.getElementType().toString());
        typeText.setStyle("-fx-font-size: 9px; -fx-fill: #555555;");
        typeText.setTextAlignment(TextAlignment.CENTER);
        typeText.setX(position.x - typeText.getLayoutBounds().getWidth() / 2);
        typeText.setY(position.y + NODE_RADIUS + 14);
        typeText.setMouseTransparent(true);

        canvasPane.getChildren().addAll(circle, centerText, typeText);

        if (node.getSons() != null) {
            for (TreeNode<EducativeElement> son : node.getSons()) {
                drawNodes(son);
            }
        }
    }

    /**
     * Construye la ventana flotante (Tooltip) con la información del curso, módulo o lección.
     */
    private Tooltip createNodeTooltip(EducativeElement element) {
        StringBuilder info = new StringBuilder();
        info.append("ID: ").append(element.getId()).append("\n");
        info.append("Tipo: ").append(element.getElementType()).append("\n");
        info.append("Título: ").append(element.getTitle() != null ? element.getTitle() : "Sin título").append("\n");
        info.append("Descripción: ").append(element.getDescription() != null ? element.getDescription() : "Sin descripción");

        if (element instanceof Lessons) {
            Lessons lesson = (Lessons) element;
            info.append("\nDuración: ").append(lesson.getDuration()).append(" min");
        }

        Tooltip tooltip = new Tooltip(info.toString());
        tooltip.setShowDelay(Duration.millis(150));
        return tooltip;
    }

    /**
     * Ajusta dinámicamente las dimensiones del canvas para activar las barras de desplazamiento si el árbol es grande.
     */
    private void adjustCanvasSize() {
        double maxX = 0;
        double maxY = 0;

        for (Point p : positions.values()) {
            if (p.x > maxX) maxX = p.x;
            if (p.y > maxY) maxY = p.y;
        }

        canvasPane.setPrefSize(maxX + MARGIN * 2, maxY + MARGIN * 2);
    }

    private String truncateText(String text, int maxLength) {
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 1) + "…";
    }

    private static class Point {
        private final double x;
        private final double y;

        private Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}