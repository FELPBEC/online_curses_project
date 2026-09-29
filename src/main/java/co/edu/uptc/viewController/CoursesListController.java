package co.edu.uptc.viewController;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

import co.edu.uptc.model.Course;
/**Clase encargada de dibujar la lista de cursos de forma dinamica
 * 
 * @author @FELPBEC
 * @version v1.0
 * @since 27/09/2026
 * 
 * 
 */
public class CoursesListController extends StackPane{
    private static final String[] COLOR_CLASSES = {
        "memo-amarillo", "memo-azul", "memo-morado",
        "memo-naranja", "memo-rosa", "memo-verde"
    };

    private TilePane contenedorCursos;
    private Consumer<Course> onCourseSelected;
    private String selectedCourseId;

    /**Método constructor de la clase que configura los contenedores donde estarán los cursos
     * creando un ScrollPane (para poder bajar y subir)
     * y un TilePane que los organizará en columnas de 3 
     */
    public CoursesListController(){
        // 1. Crear y configurar el ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true); // Evita scroll horizontal
        scrollPane.setFitToHeight(false);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("catalog-scroll-pane");
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // 3. Crear y configurar el TilePane (La cuadrícula)
        contenedorCursos = new TilePane();
        contenedorCursos.setPrefColumns(3); // 3 columnas fijas
        contenedorCursos.setHgap(22);
        contenedorCursos.setVgap(22);
        contenedorCursos.setMaxWidth(Double.MAX_VALUE);
        contenedorCursos.setStyle("-fx-padding: 24;");
        widthProperty().addListener((observable, previous, current) -> {
            int columns = Math.max(1, (int) ((current.doubleValue() - 70) / 252));
            contenedorCursos.setPrefColumns(Math.min(4, columns));
        });

        // 4. Ensamblar el componente
        scrollPane.setContent(contenedorCursos);
        this.getChildren().add(scrollPane);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }
    
    /**Método que dibuja una lista de cursos en forma de memofichas
     * diviendo la lista en grupos de tres a lo ancho de la pantalla
     * 
     * @param courses lista de cursos enviada ej: los cursos inscritos por un estudiante
     */
    public void DrawCoursesList(List<Course> courses){
        contenedorCursos.getChildren().clear();
        if (courses == null) {
            return;
        }
        int indexColor = 0;
        for (Course curso : courses) {
            javafx.scene.control.Button memoCard = new javafx.scene.control.Button();
            memoCard.setPrefSize(230, 210);
            memoCard.setMaxSize(230, 210);
            memoCard.setAccessibleText(curso.getTitle());
            memoCard.setUserData(curso.getId());
            String colorClass = COLOR_CLASSES[indexColor % COLOR_CLASSES.length];
            memoCard.getStyleClass().addAll(
                    "memoficha-base", colorClass);
            if (curso.getId().equals(selectedCourseId)) {
                memoCard.getStyleClass().add("memo-selected");
            }
            memoCard.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);

            VBox contenido = new VBox(10);
            Label title = new Label(curso.getTitle());
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #27364b;");
            title.setWrapText(true);
            Label id = new Label(curso.getId());
            id.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #27364b;");
            Label description = new Label(curso.getDescription() == null ? "" : curso.getDescription());
            description.setStyle("-fx-text-fill: #27364b;");
            description.setWrapText(true);
            contenido.getChildren().addAll(title, id, description);
            contenido.setMouseTransparent(true);
            memoCard.setGraphic(contenido);
            memoCard.setOnAction(event -> {
                selectedCourseId = curso.getId();
                updateSelectedCard();
                sendCourseSelectedId(curso.getId());
                if (onCourseSelected != null) {
                    onCourseSelected.accept(curso);
                }
            });
            contenedorCursos.getChildren().add(memoCard);
            indexColor++;
        }
    }

    public void setOnCourseSelected(Consumer<Course> onCourseSelected) {
        this.onCourseSelected = onCourseSelected;
    }

    public void clearSelectedCourse() {
        selectedCourseId = null;
        updateSelectedCard();
    }

    private void updateSelectedCard() {
        for (javafx.scene.Node node : contenedorCursos.getChildren()) {
            if (node instanceof javafx.scene.control.Button) {
                javafx.scene.control.Button card = (javafx.scene.control.Button) node;
                if (java.util.Objects.equals(card.getUserData(), selectedCourseId)) {
                    if (!card.getStyleClass().contains("memo-selected")) {
                        card.getStyleClass().add("memo-selected");
                    }
                } else {
                    card.getStyleClass().remove("memo-selected");
                }
            }
        }
    }

    public String sendCourseSelectedId(String idCourseSelected){
        return idCourseSelected;
    }
}
