package co.edu.uptc.controller;

import co.edu.uptc.exceptions.UserNotFoundException;
import co.edu.uptc.exceptions.WrongPasswordException;
import co.edu.uptc.model.Course;
import co.edu.uptc.model.Estudent;
import co.edu.uptc.model.Teacher;
/**Clase controlador maestro que sirve de enlace para los 3 controladores principales y las vistas
 * Define además propiedades globales del sístema como:
 * El actual estudiante logueado
 * El actual profesor logueado
 * El actual curso visualizado
 * 
 * @author @FELPBEC
 * @version v1.0
 * @since 22/09/2026
 */
public class GeneralController {
    private  final CourseController courseController;
    private  Course currentCourse;
    private  final EstudentController estudentController;
    private  Estudent currentEstudent;
    private  final TeacherController teacherController;
    private  Teacher currentTeacher;
    /**Método constructor de la clase GeneralController que inicializa los 3 controladores principales
     * 
     * @param courseController controlador de cursos (árbol n-ario)
     * @param estudentController controlador de estudiantes
     * @param teacherController controlador de profesores
     */
    public GeneralController(CourseController courseController, EstudentController estudentController,
            TeacherController teacherController) {
        this.courseController = courseController;
        this.estudentController = estudentController;
        this.teacherController = teacherController;
        this.currentEstudent=null;
        this.currentTeacher=null;
        this.currentCourse=null;
    }
    /**Método que envía el curso actual visualizado 
     * 
     * @return curso actual
     */
    public Course getCurrentCourse() {
        return currentCourse;
    }
    /**Método establece el curso actual visualizado
     * 
     * @param currentCourse curso actual
     */
    public void setCurrentCourse(Course currentCourse) {
        this.currentCourse = currentCourse;
    }
    /**Método que envía el estudiante actual  
     * 
     * @return el estudiante actual
     */
    public Estudent getCurrentEstudent() {
        return currentEstudent;
    }
    /**Método que establece el estudiante actual
     * 
     * @param currentEstudent estudiante actual
     */
    public void setCurrentEstudent(Estudent currentEstudent) {
        this.currentEstudent = currentEstudent;
    }
    /**Método que envía el profesor actual
     * 
     * @return el profesor actual
     */
    public Teacher getCurrentTeacher() {
        return currentTeacher;
    }
    /**Método que establece el profesor actual
     * 
     * @param currentTeacher el profesor actual
     */
    public void setCurrentTeacher(Teacher currentTeacher) {
        this.currentTeacher = currentTeacher;
    }
    

    //LÓGICA DE LOGIN
    /**Método para loguearse como profesor 
     * y establecerlo como profesor actual
     * 
     * @param userOrEmail nombre o correo del profesor
     * @param password contraseña del profesor
     * @throws UserNotFoundException excepción en caso de que el nombre de usuario o contraseña no se encuentren
     * @throws WrongPasswordException excepción en caso de que la contraseña sea incorrecta
     */
    public void teacherLogin(String userOrEmail, String password) throws UserNotFoundException,WrongPasswordException{
        Teacher teacherLogin= teacherController.joinTeacherAcount(userOrEmail, password);
        if (teacherLogin!=null) {
            setCurrentTeacher(teacherLogin);
        }
    }
    /**Método para loguearse como estudiante
     * y establecerlo como estudiante actual
     * 
     * @param userOrEmail nombre o correo del estudiante 
     * @param password contraseña del estudiante 
     * @throws UserNotFoundException excepción en caso de que el nombre de usuario o contraseña no se encuentren
     * @throws WrongPasswordException excepción en caso de que la contraseña sea incorrecta
     */
    public void estudentLogin(String userOrEmail,String password) throws UserNotFoundException,WrongPasswordException{
        Estudent estudent=estudentController.joinEstudentAcount(userOrEmail, password);
        if (estudent!=null) {
            setCurrentEstudent(estudent);
        }
    }
    
    
}
