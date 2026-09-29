package co.edu.uptc.controller;

import java.util.ArrayList;
import java.util.List;

import co.edu.uptc.exceptions.NoAvaliableLessonsInTheCourseException;
import co.edu.uptc.exceptions.CredentialsAlreadyExistException;
import co.edu.uptc.exceptions.InvalidFortmatException;
import co.edu.uptc.exceptions.SavedFailureException;
import co.edu.uptc.exceptions.UserNotFoundException;
import co.edu.uptc.exceptions.WrongPasswordException;
import co.edu.uptc.interfaces.EducativeElement;
import co.edu.uptc.interfaces.EducativeElementType;
import co.edu.uptc.model.Course;
import co.edu.uptc.model.CourseProgress;
import co.edu.uptc.model.Estudent;
import co.edu.uptc.model.Teacher;
import co.edu.uptc.model.TreeNode;
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

    public Estudent registerEstudent(String userName, String email, String password) {
        Estudent estudent = estudentController.registerEstudent(userName, email, password);
        setCurrentEstudent(estudent);
        return estudent;
    }

    public Teacher registerTeacher(String userName, String email, String password)
            throws InvalidFortmatException, CredentialsAlreadyExistException {
        Teacher teacher = teacherController.registerTeacher(userName, email, password);
        setCurrentTeacher(teacher);
        return teacher;
    }

    public void setCurrentCourseById(String idCourse) {
        currentCourse = courseController.findCourse(idCourse);
    }

    public Course createCourseForCurrentTeacher(String title, String description) {
        if (currentTeacher == null) {
            throw new IllegalStateException("A teacher must be authenticated to create a course.");
        }
        if (title == null || title.isBlank() || description == null || description.isBlank()) {
            throw new IllegalArgumentException("Course title and description are required.");
        }

        Course course = courseController.addCourse(title.trim(), description.trim());
        teacherController.addNewAssignedCourse(currentTeacher, course.getId());
        try {
            courseController.saveChanges();
            teacherController.saveAll();
        } catch (SavedFailureException e) {
            teacherController.removeAssignedCourse(currentTeacher, course.getId());
            courseController.deleteCourse(course.getId());
            try {
                courseController.saveChanges();
            } catch (SavedFailureException rollbackFailure) {
                e.addSuppressed(rollbackFailure);
            }
            throw e;
        }
        return course;
    }

    public void addModuleToCurrentTeacherCourse(
            String courseId, String parentId, String title, String description) {
        Course course = requireAssignedCourse(courseId);
        if (title == null || title.isBlank() || description == null || description.isBlank()) {
            throw new IllegalArgumentException("Module title and description are required.");
        }
        TreeNode<EducativeElement> parentNode = findNode(course.getRoot(), parentId);
        if (parentNode == null || parentNode.getData() == null
                || (parentNode.getData().getElementType() != EducativeElementType.COURSE
                        && parentNode.getData().getElementType() != EducativeElementType.MODULO)) {
            throw new IllegalArgumentException("Modules can only be added to a course or module.");
        }

        int originalChildCount = parentNode.getSons().size();
        courseController.addModule(courseId, parentId, title.trim(), description.trim());
        saveCourseTreeChange(parentNode, originalChildCount);
    }

    public void addLessonToCurrentTeacherCourse(
            String courseId, String parentId, String title, String description, double duration) {
        Course course = requireAssignedCourse(courseId);
        if (title == null || title.isBlank() || description == null || description.isBlank()) {
            throw new IllegalArgumentException("Lesson title and description are required.");
        }
        if (!Double.isFinite(duration) || duration <= 0) {
            throw new IllegalArgumentException("Lesson duration must be a positive number.");
        }
        TreeNode<EducativeElement> parentNode = findNode(course.getRoot(), parentId);
        if (parentNode == null || parentNode.getData() == null
                || parentNode.getData().getElementType() != EducativeElementType.MODULO) {
            throw new IllegalArgumentException("Lessons can only be added to a module.");
        }

        int originalChildCount = parentNode.getSons().size();
        courseController.addLesson(courseId, parentId, title.trim(), description.trim(), duration);
        saveCourseTreeChange(parentNode, originalChildCount);
    }

    private Course requireAssignedCourse(String courseId) {
        if (currentTeacher == null) {
            throw new IllegalStateException("A teacher must be authenticated to manage course content.");
        }
        List<String> assignedCourseIds = currentTeacher.getAsginedCourses();
        if (assignedCourseIds == null || !assignedCourseIds.contains(courseId)) {
            throw new IllegalArgumentException("The course is not assigned to the current teacher.");
        }
        Course course = courseController.findCourse(courseId);
        if (course == null) {
            throw new IllegalArgumentException("The assigned course could not be found.");
        }
        return course;
    }

    private TreeNode<EducativeElement> findNode(
            TreeNode<EducativeElement> node, String elementId) {
        if (node == null || node.getData() == null) {
            return null;
        }
        if (node.getData().getId().equals(elementId)) {
            return node;
        }
        for (TreeNode<EducativeElement> child : node.getSons()) {
            TreeNode<EducativeElement> result = findNode(child, elementId);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private void saveCourseTreeChange(TreeNode<EducativeElement> parentNode, int originalChildCount) {
        try {
            courseController.saveChanges();
        } catch (SavedFailureException e) {
            while (parentNode.getSons().size() > originalChildCount) {
                parentNode.getSons().remove(parentNode.getSons().size() - 1);
            }
            throw e;
        }
    }

    public List<Course> getCourseList() {
        return new ArrayList<>(courseController.getCourseList());
    }

    public List<Course> getCoursesForCurrentStudent(boolean enrolled) {
        if (currentEstudent == null) {
            throw new IllegalStateException("A student must be authenticated to browse courses.");
        }
        List<Course> courses = new ArrayList<>();
        for (Course course : courseController.getCourseList()) {
            if (course != null && currentEstudent.isRegisterOnCourse(course.getId()) == enrolled) {
                courses.add(course);
            }
        }
        return courses;
    }

    public boolean registerCurrentStudentOnCourse(String courseId) {
        if (currentEstudent == null) {
            throw new IllegalStateException("A student must be authenticated to enroll in a course.");
        }
        Course course = courseController.findCourse(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course does not exist: " + courseId);
        }
        if (currentEstudent.isRegisterOnCourse(courseId)) {
            return false;
        }

        String firstLessonId = courseController.getIdFirstLesson(courseId);
        currentCourse = course;
        currentEstudent.registerCourse(courseId, firstLessonId);
        try {
            estudentController.saveAll();
        } catch (SavedFailureException e) {
            currentEstudent.getCoursesProgress().remove(courseId);
            throw e;
        }
        return true;
    }

    public boolean completeCurrentStudentLesson(String courseId) {
        if (currentEstudent == null || !currentEstudent.isRegisterOnCourse(courseId)) {
            throw new IllegalStateException("The current student is not enrolled in this course.");
        }
        Course course = courseController.findCourse(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course does not exist: " + courseId);
        }
        CourseProgress progress = currentEstudent.getCoursesProgress().get(courseId);
        if (progress.isCompleteState()) {
            return true;
        }

        String previousLessonId = progress.getIdLesson();
        boolean completed;
        try {
            String nextLessonId = courseController.getIdNextLesson(courseId, previousLessonId);
            progress.setIdLesson(nextLessonId);
            completed = false;
        } catch (NoAvaliableLessonsInTheCourseException e) {
            progress.setCompleteState(true);
            completed = true;
        }

        try {
            estudentController.saveAll();
        } catch (SavedFailureException e) {
            progress.setIdLesson(previousLessonId);
            progress.setCompleteState(false);
            throw e;
        }
        currentCourse = course;
        return completed;
    }

    public String getCurrentLessonId(String courseId) {
        if (currentEstudent == null || !currentEstudent.isRegisterOnCourse(courseId)) {
            return null;
        }
        return currentEstudent.getCoursesProgress().get(courseId).getIdLesson();
    }

    public boolean isCurrentStudentCourseComplete(String courseId) {
        if (currentEstudent == null || !currentEstudent.isRegisterOnCourse(courseId)) {
            return false;
        }
        return currentEstudent.getCoursesProgress().get(courseId).isCompleteState();
    }

    public double getCurrentStudentCourseProgressPercent(String courseId) {
        if (currentEstudent == null || !currentEstudent.isRegisterOnCourse(courseId)) {
            return 0.0;
        }
        Course course = courseController.findCourse(courseId);
        if (course == null) {
            return 0.0;
        }
        if (isCurrentStudentCourseComplete(courseId)) {
            return 100.0;
        }
        String currentLessonId = getCurrentLessonId(courseId);
        return courseController.getPercentOfLessonsComplete(courseId, currentLessonId);
    }

    public List<Course> sendCourseListAsignedToTeacher() {
        if (currentTeacher == null) {
            throw new IllegalStateException("A teacher must be authenticated to view assigned courses.");
        }
        List<Course> assignedCourses = new ArrayList<>();
        List<String> courseIds = currentTeacher.getAsginedCourses();
        if (courseIds == null) {
            return assignedCourses;
        }
        for (String courseId : courseIds) {
            Course course = courseController.findCourse(courseId);
            if (course != null) {
                assignedCourses.add(course);
            }
        }
        return assignedCourses;
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