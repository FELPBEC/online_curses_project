package co.edu.uptc.model;
import co.edu.uptc.abstracts.User;

import java.util.HashMap;
import java.util.Map;

/**Clase estudiante que desciende de la clase abstracta {@link User} 
 * y que además de los atributos propios de la clase posee una lista de ids de los cursos a los que esta inscrito
 * junto con el estado de completado de la misma {@link CourseProgress}
 * @author @FELPBEC
 * @version v1.0
 * @since 19/09/2026
 */
public class Estudent extends User{
    //El primer string es la id del curso
    private Map<String,CourseProgress> coursesProgress;
    /**Método constructor vacío para poder cargar los estudiantes desde persistencia
     * 
     */
    public Estudent() {
    }
    /**Método constructor con párametros para la creación de un Estudiante mediante ingreso de datos
     * 
     * @param id identificador númerico
     * @param userName  nombre de usuario
     * @param email correo electronico
     * @param password  contraseña 
     */
    public Estudent(int id, String userName, String email, String password) {
        super(userName, email, password, id);
        this.coursesProgress=new HashMap<>();
    }
    /**Método para obtener el mapa de progreso de cursos
     * 
     * @return mapa de progreso de curso
     */
    public Map<String, CourseProgress> getCoursesProgress() {
        return coursesProgress;
    }
    /**Método para establecer el mapa de progreso de cursos
     * 
     * @param coursesProgress mapa de progreso de cursos
     */
    public void setCoursesProgress(Map<String, CourseProgress> coursesProgress) {
        this.coursesProgress = coursesProgress;
    }
    /**Método para incribirse en un nuevo curso
     * Actualiza el Map
     * 
     * @param idCourse id del curso al que quiere inscribirse
     * @param idFirstLesson id de la primera lección que contiene el curso
     */
    public void registerCourse(String idCourse, String idFirstLesson){
        coursesProgress.put(idCourse, new CourseProgress(idFirstLesson));
    }
    /**Método para obtener la id de la lección en la que va el estudiante de un determinado curso
     * 
     * @param idCourse id del curso del que se requiere la información
     * @return el id de la lección en la que va el estudiante de un curso
     */
    public String getLessonOnCourse(String idCourse){
        return coursesProgress.get(idCourse).getIdLesson();

    }
    /**Método para actualizar la lección en la que va el estudiante a la siguiente
     * 
     * @param idCourse id del curso en donde se va a buscar
     * @param idNextLesson id de la siguiente lección
     */
    public void goToNextLesson(String idCourse, String idNextLesson){
        coursesProgress.get(idCourse).setIdLesson(idNextLesson);
    }
    /**Método para indicar el completado de un curso
     * estableciendo su estado de falso a verdadero
     *  
     * @param idCourse id del curso completado
     */
    public void completeCourse(String idCourse){
        coursesProgress.get(idCourse).setCompleteState(true);
    }
    
    
}
