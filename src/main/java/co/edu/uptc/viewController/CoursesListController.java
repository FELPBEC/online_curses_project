package co.edu.uptc.viewController;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.util.List;

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
    private TilePane contenedorCursos;

    /**Método constructor de la clase que configura los contenedores donde estarán los cursos
     * creando un ScrollPane (para poder bajar y subir)
     * y un TilePane que los organizará en columnas de 3 
     */
    public CoursesListController(){
        // 1. Crear y configurar el ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true); // Evita scroll horizontal
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // 3. Crear y configurar el TilePane (La cuadrícula)
        contenedorCursos = new TilePane();
        contenedorCursos.setPrefColumns(3); // 3 columnas fijas
        contenedorCursos.setHgap(30);
        contenedorCursos.setVgap(30);
        // Margen para no pisar el argollado ni la línea roja
        contenedorCursos.setStyle("-fx-padding: 40 40 40 50;"); 

        // 4. Ensamblar el componente
        scrollPane.setContent(contenedorCursos);
        this.getChildren().add(scrollPane);
    }
    
    /**Método que dibuja una lista de cursos en forma de memofichas
     * diviendo la lista en grupos de tres a lo ancho de la pantalla
     * 
     * @param courses lista de cursos enviada ej: los cursos inscritos por un estudiante
     */
    public void DrawCoursesList(List<Course> courses){
        String[] clasesColores = {
            "memo-verde", "memo-azul", "memo-rosa", 
            "memo-amarillo", "memo-morado", ".memo-naranja"
        };

        int indexColor = 0;
        // 2. Recorremos la lista de cursos obtenidos del JSON
        for (Course curso : courses) {
            
            // a) Crear el contenedor principal de la memoficha
            StackPane memoficha = new StackPane();
            memoficha.setPrefSize(280, 280); // Tamaño fijo

            // b) Asignar las clases CSS: La base + El color rotativo
            String colorAsignado = clasesColores[indexColor % clasesColores.length];
            memoficha.getStyleClass().addAll("memoficha-base", colorAsignado);

            // c) Crear el contenido (Textos)
            VBox contenido = new VBox(10);
            Label tittle = new Label(curso.getTitle());
            tittle.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
            Label id = new Label(curso.getId());
            id.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
            Label descripcion = new Label(curso.getDescription());
            descripcion.setWrapText(true); // Para que el texto baje de línea si es largo
            contenido.getChildren().addAll(tittle,id, descripcion);
            memoficha.getChildren().add(contenido);

            memoficha.setOnMouseClicked(event -> {
                System.out.println("Abriendo curso: " + curso.getId());
                 sendCourseSelectedId(curso.getId());
            });

            // e) Añadir la memoficha terminada al TilePane
            contenedorCursos.getChildren().add(memoficha);

            // f) Aumentar el índice para que el siguiente curso tenga el siguiente color
            indexColor++;
        }
    }
    //TODO:No hay algo que hacer aquí es solo para que veas, este es el método que envía el ID del seleccionado
    public String sendCourseSelectedId(String idCourseSelected){
        return idCourseSelected;
    }
}
