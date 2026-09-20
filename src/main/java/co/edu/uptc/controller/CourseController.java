package co.edu.uptc.controller;

import java.util.List;
import co.edu.uptc.interfaces.Repository;
import co.edu.uptc.model.Course;
/**Clase CourseController que se encarga de:
 * 1. Manejar el CRUD de cursos
 * 2. Manejar el CRUD de modulos
 * 3. Manejar el CRUD de lecciones
 * 
 * @author @jm1407db
 * @version v1.0
 * @since 20/09/2026
 */
public class CourseController {
    private final Repository<Course> repository;
    private List<Course> courseList;
    /**Método constructor para la clase CourseController
     * que inicializa la lista en memoria por medio del método sendAll() del repositorio que se envía como párametro
     *  
     * @param repository repositorio de cualquier tipo que maneja la persistencia 
     */
    public CourseController(Repository<Course> repository) {
        this.repository = repository;
        this.courseList = repository.sendAll();
    }
    
}
