package co.edu.uptc.controller;

import co.edu.uptc.exceptions.NoAvaliableLessonsInTheCourseException;
import co.edu.uptc.exceptions.UserNotFoundException;
import co.edu.uptc.exceptions.WrongPasswordException;
import co.edu.uptc.model.Course;
import co.edu.uptc.model.Estudent;
import co.edu.uptc.model.Teacher;
import co.edu.uptc.persistence.CoursesJsonRepository;
import co.edu.uptc.persistence.EstudentJsonRepository;
import co.edu.uptc.persistence.TeacherJsonRepository;
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
    public GeneralController() {
        this.courseController = new CourseController( new CoursesJsonRepository("Courses.json"));
        this.estudentController = new EstudentController(new EstudentJsonRepository("Estudents.json"));
        this.teacherController = new TeacherController(new TeacherJsonRepository("Teachers.json"));
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
    

    //LÓGICA DE LOGIN/Establecer los objetos actuales
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
    /**Método que establece el curso actual según la id
     * 
     * @param idCourse id del curso: COR-1
     */
    public void setCurrentCourseById(String idCourse){
        currentCourse=courseController.findCourse(idCourse);
    }

    //LÓGICA DE PROGRESO ENTRE CURSOS
    /**Método para registrar al estudiante actual en el curso actual
     * 
     */
    public void registerOnCourse(){
        currentEstudent.registerCourse(currentCourse.getId(), courseController.getIdFirstLesson(currentCourse.getId()));
    }
    /**Método cuando se indica que se completo una lección y se pasa a la siguiente
     * Se asigna la id de la siguiente lección en el Map del estudiante actual
     * en caso de que no haya más lecciones, actualizará el estado del curso como completado
     * 
     */
    public void goToNextLesson()throws NoAvaliableLessonsInTheCourseException{
        String idCourse=currentCourse.getId();
        String idCurrentLesson=currentEstudent.getCoursesProgress().get(idCourse).getIdLesson();
        try {
            String idNextLesson=courseController.getIdNextLesson(idCourse, idCurrentLesson);
            currentEstudent.goToNextLesson(idCourse, idNextLesson);
        } catch ( NoAvaliableLessonsInTheCourseException e) {
            currentEstudent.completeCourse(idCourse);
            throw e;
        }
    }
    /**Método que envía el porcentaje de completado de las lecciones totales del curso
     * 
     * @return número de porcentaje de completado ej: 43,5
     */
    public double sendPercentLesson(){
        String idCourse=currentCourse.getId();
        String idCurrentLesson=currentEstudent.getCoursesProgress().get(idCourse).getIdLesson();
        return courseController.getPercentOfLessonsComplete(idCourse, idCurrentLesson);
    }
    

}
