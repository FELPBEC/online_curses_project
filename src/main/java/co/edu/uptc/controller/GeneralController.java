package co.edu.uptc.controller;

import java.util.ArrayList;
import java.util.List;

import co.edu.uptc.exceptions.NoAvaliableLessonsInTheCourseException;
import co.edu.uptc.exceptions.UserNotFoundException;
import co.edu.uptc.exceptions.WrongPasswordException;
import co.edu.uptc.interfaces.EducativeElement;
import co.edu.uptc.interfaces.EducativeElementType;
import co.edu.uptc.model.Course;
import co.edu.uptc.model.Estudent;
import co.edu.uptc.model.Teacher;
import co.edu.uptc.persistence.CoursesJsonRepository;
import co.edu.uptc.persistence.EstudentJsonRepository;
import co.edu.uptc.persistence.TeacherJsonRepository;

/**
 * Clase controlador maestro que sirve de enlace para los 3 controladores principales y las vistas.
 * Define además propiedades globales del sistema como:
 * El actual estudiante logueado, profesor logueado y curso visualizado.
 * 
 * @author @FELPBEC
 * @version v1.1
 * @since 22/09/2026
 */
public class GeneralController {
    private final CourseController courseController;
    private Course currentCourse;
    private final EstudentController estudentController;
    private Estudent currentEstudent;
    private final TeacherController teacherController;
    private Teacher currentTeacher;

    /**
     * Método constructor de la clase GeneralController que inicializa los 3 controladores principales.
     */
    public GeneralController() {
        this.courseController = new CourseController(new CoursesJsonRepository("Courses.json"));
        this.estudentController = new EstudentController(new EstudentJsonRepository("Estudents.json"));
        this.teacherController = new TeacherController(new TeacherJsonRepository("Teachers.json"));
        this.currentEstudent = null;
        this.currentTeacher = null;
        this.currentCourse = null;
    }

    public Course getCurrentCourse() {
        return currentCourse;
    }

    public void setCurrentCourse(Course currentCourse) {
        this.currentCourse = currentCourse;
    }

    public Estudent getCurrentEstudent() {
        return currentEstudent;
    }

    public void setCurrentEstudent(Estudent currentEstudent) {
        this.currentEstudent = currentEstudent;
    }

    public Teacher getCurrentTeacher() {
        return currentTeacher;
    }

    public void setCurrentTeacher(Teacher currentTeacher) {
        this.currentTeacher = currentTeacher;
    }

    // =========================================================================
    // LÓGICA DE LOGIN / AUTENTICACIÓN
    // =========================================================================

    public void teacherLogin(String userOrEmail, String password) throws UserNotFoundException, WrongPasswordException {
        Teacher teacherLogin = teacherController.joinTeacherAcount(userOrEmail, password);
        if (teacherLogin != null) {
            setCurrentTeacher(teacherLogin);
        }
    }

    public void estudentLogin(String userOrEmail, String password) throws UserNotFoundException, WrongPasswordException {
        Estudent estudent = estudentController.joinEstudentAcount(userOrEmail, password);
        if (estudent != null) {
            setCurrentEstudent(estudent);
        }
    }

    public void setCurrentCourseById(String idCourse) {
        currentCourse = courseController.findCourse(idCourse);
    }

    // =========================================================================
    // LÓGICA DE PROGRESO ENTRE CURSOS Y LECCIONES
    // =========================================================================

    public void registerOnCourse() {
        currentEstudent.registerCourse(currentCourse.getId(), courseController.getIdFirstLesson(currentCourse.getId()));
    }

    public void goToNextLesson(Estudent estudent) throws NoAvaliableLessonsInTheCourseException {
        String idCourse = currentCourse.getId();
        String idCurrentLesson = estudent.getLessonOnCourse(idCourse);
        try {
            String idNextLesson = courseController.getIdNextLesson(idCourse, idCurrentLesson);
            estudent.goToNextLesson(idCourse, idNextLesson);
        } catch (NoAvaliableLessonsInTheCourseException e) {
            estudent.completeCourse(idCourse);
            throw e;
        }
    }

    public double sendPercentLesson() {
        String idCourse = currentCourse.getId();
        String idCurrentLesson = currentEstudent.getLessonOnCourse(idCourse);
        return courseController.getPercentOfLessonsComplete(idCourse, idCurrentLesson);
    }

    public void removeCurrentCourse() {
        String idCourseToRemove = currentCourse.getId();
        estudentController.removeCourseForStudents(idCourseToRemove);
        currentTeacher.removeAsignedCourse(idCourseToRemove);
        courseController.deleteCourse(idCourseToRemove);
    }

    // =========================================================================
    // LÓGICA DE ELIMINACIÓN DE LECCIONES Y MÓDULOS CON REASIGNACIÓN DE ESTUDIANTES
    // =========================================================================

    /**
     * Método auxiliar que retorna la lista de estudiantes inscritos a una lección específica dentro del curso actual.
     * 
     * @param idLesson id de la lección
     * @return lista de estudiantes actualmente posicionados en esa lección
     */
    private List<Estudent> getEstudentListByLesson(String idLesson) {
        String idCourse = currentCourse.getId();
        List<Estudent> estudents = new ArrayList<>();
        for (Estudent estudent : estudentController.getEstudentList()) {
            if (estudent.isRegisterOnCourse(idCourse)) {
                if (idLesson.equals(estudent.getLessonOnCourse(idCourse))) {
                    estudents.add(estudent);
                }
            }
        }
        return estudents;
    }

    /**
     * Elimina una lección del curso actual.
     * Verifica todos los estudiantes inscritos; si algún estudiante está en la lección a eliminar,
     * lo pasa a la siguiente lección. Si era la última lección, marca el curso como completado.
     * 
     * @param idLessonToRemove ID de la lección a eliminar.
     */
    public void removeLesson(String idLessonToRemove) {
        if (currentCourse == null) return;
        String idCourse = currentCourse.getId();

        // 1. Obtener la lección siguiente ANTES de eliminarla del árbol
        String idNextLesson = null;
        try {
            idNextLesson = courseController.getIdNextLesson(idCourse, idLessonToRemove);
        } catch (NoAvaliableLessonsInTheCourseException e) {
            idNextLesson = null; // No hay más lecciones posteriores
        }

        // 2. Reasignar a todos los estudiantes afectados por la eliminación
        List<Estudent> affectedStudents = getEstudentListByLesson(idLessonToRemove);
        for (Estudent estudent : affectedStudents) {
            if (idNextLesson != null) {
                estudent.goToNextLesson(idCourse, idNextLesson);
            } else {
                estudent.completeCourse(idCourse);
            }
        }

        // 3. Eliminar físicamente la lección
        courseController.deleteLesson(idCourse, idLessonToRemove);
    }

    /**
     * Elimina un módulo del curso actual y todas sus lecciones contenidas.
     * Si algún estudiante se encuentra en una lección dentro de este módulo,
     * lo transfiere a la primera lección del siguiente módulo.
     * Si no hay lecciones posteriores en el curso, marca el curso como completado.
     * 
     * @param idModuleToRemove ID del módulo a eliminar.
     */
    public void removeModule(String idModuleToRemove) {
        if (currentCourse == null) return;
        String idCourse = currentCourse.getId();

        // 1. Obtener todas las lecciones que pertenecen al módulo que se eliminará
        List<String> lessonsInModule = getLessonIdsInModule(idCourse, idModuleToRemove);

        if (!lessonsInModule.isEmpty()) {
            // La última lección del módulo nos ayuda a encontrar cuál es la lección del SIGUIENTE módulo
            String lastLessonInModule = lessonsInModule.get(lessonsInModule.size() - 1);
            String idNextLessonAfterModule = null;

            try {
                idNextLessonAfterModule = courseController.getIdNextLesson(idCourse, lastLessonInModule);
            } catch (NoAvaliableLessonsInTheCourseException e) {
                idNextLessonAfterModule = null; // No hay más módulos/lecciones después de este
            }

            // 2. Reasignar a los estudiantes que estén en CUALQUIERA de las lecciones del módulo
            for (String idLesson : lessonsInModule) {
                List<Estudent> affectedStudents = getEstudentListByLesson(idLesson);
                for (Estudent estudent : affectedStudents) {
                    if (idNextLessonAfterModule != null) {
                        estudent.goToNextLesson(idCourse, idNextLessonAfterModule);
                    } else {
                        estudent.completeCourse(idCourse);
                    }
                }
            }
        }

        // 3. Eliminar físicamente el módulo y sus nodos hijos
        courseController.deleteModule(idCourse, idModuleToRemove);
    }

    /**
     * Método auxiliar que obtiene todos los IDs de lecciones contenidas en un módulo.
     * 
     * @param idCourse ID del curso
     * @param idModuleId ID del módulo a consultar
     * @return Lista con los IDs de las lecciones del módulo
     */
    private List<String> getLessonIdsInModule(String idCourse, String idModuleId) {
        List<String> lessonIds = new ArrayList<>();
        List<EducativeElement> elements = courseController.getPreOrder(idCourse);

        boolean insideModule = false;
        for (EducativeElement element : elements) {
            if (element.getId().equals(idModuleId)) {
                insideModule = true;
                continue;
            }
            if (insideModule) {
                // Si vuelve a encontrar otro módulo al mismo nivel superior, ha salido del subárbol
                if (element.getElementType() == EducativeElementType.MODULO && !element.getId().startsWith(idModuleId)) {
                    // Módulo diferente detectado fuera de la jerarquía
                    insideModule = false;
                    break;
                }
                if (element.getElementType() == EducativeElementType.LESSON) {
                    lessonIds.add(element.getId());
                }
            }
        }
        return lessonIds;
    }
}