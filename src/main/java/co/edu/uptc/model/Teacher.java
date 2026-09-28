package co.edu.uptc.model;
import java.util.ArrayList;
import java.util.List;

import co.edu.uptc.abstracts.User;
/**Clase que representa a los profesores que desciende de la clase {@link User} 
 * 
 * @author @FELPBEC
 * @version v1.1
 * @since 19/09/2026
 */
public class Teacher extends User{
    private List<String> asginedCourses;
    /**Constructor vacío de la clase admin para cargar desde persistencia
     * 
     */
    public Teacher() {
    }
    /**Método constructor con párametros para crear un administrador mediante ingreso de datos
     * 
     * @param userName nombre de usuario del administrador
     * @param email correo electronico del administrador
     * @param password contraseña del administrador
     * @param id    identificador del administrador
     */
    public Teacher( int id, String userName, String email, String password) {
        super(userName, email, password, id);
        asginedCourses=new ArrayList<>();
    }
    /**Método para obtener la lista de ids de cursos asignados por el profesor
     * 
     * @return la lista de ids de cursos asignados al profesor
     */
    public List<String> getAsginedCourses() {
        return asginedCourses;
    }
    /**Método para establecer la lista de cursos asignados al profesor
     * 
     * @param asginedCourses lista de cursos asignados
     */
    public void setAsginedCourses(List<String> asginedCourses) {
        this.asginedCourses = asginedCourses;
    }
    /**Método que añade un nuevo curso a la lista de cursos asginados al profesor por medio de su id
     * 
     * @param idCourse id del curso asignado
     */
    public void addNewCourse(String idCourse){
        asginedCourses.add(idCourse);
    }
    /**Método que elimina un curso asignado al profesor de la lista
     * 
     * @param idCourse id del curso a eliminar
     */
    public void removeAsignedCourse(String idCourse){
        asginedCourses.remove(idCourse);
    }
    
    
}
