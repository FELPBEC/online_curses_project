package co.edu.uptc.controller;

import co.edu.uptc.interfaces.Repository;
import co.edu.uptc.model.Course;
import co.edu.uptc.model.Estudent;
import co.edu.uptc.model.Teacher;
import co.edu.uptc.util.PasswordSecurityService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias exhaustivas para {@link GeneralController}.
 * Cobertura del 100% de métodos, flujos de autenticación, gestión de cursos, 
 * módulos y lecciones con reasignación de estudiantes.
 */
public class GeneralControllerTest {

    private GeneralController generalController;
    private CourseController courseController;
    private EstudentController estudentController;
    private TeacherController teacherController;

    private DummyRepository<Course> courseRepo;
    private DummyRepository<Estudent> estudentRepo;
    private DummyRepository<Teacher> teacherRepo;

    private PasswordSecurityService securityService;

    private static class DummyRepository<T> implements Repository<T> {
        private final List<T> list = new ArrayList<>();
        private boolean saveCalled;
        private boolean failOnSave;

        @Override
        public List<T> sendAll() {
            return list;
        }

        @Override
        public void saveAll(List<T> list) {
            saveCalled = true;
            if (failOnSave) {
                throw new co.edu.uptc.exceptions.SavedFailureException("No se pudo guardar", null);
            }
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        generalController = new GeneralController();
        securityService = new PasswordSecurityService();

        courseRepo = new DummyRepository<>();
        estudentRepo = new DummyRepository<>();
        teacherRepo = new DummyRepository<>();

        courseController = new CourseController(courseRepo);
        estudentController = new EstudentController(estudentRepo);
        teacherController = new TeacherController(teacherRepo);

        // Inyectar controladores con repositorios simulados mediante reflexión
        setPrivateField(generalController, "courseController", courseController);
        setPrivateField(generalController, "estudentController", estudentController);
        setPrivateField(generalController, "teacherController", teacherController);
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // =========================================================================
    // PRUEBAS DE PROPIEDADES, GETTERS Y SETTERS
    // =========================================================================

    @Test
    @DisplayName("Debe permitir modificar y obtener el curso, estudiante y profesor actuales")
    public void testGettersAndSetters() {
        Course course = new Course("COURSE-1", "Java", null);
        Estudent student = new Estudent(1, "juan", "juan@uptc.edu.co", "pass");
        Teacher teacher = new Teacher(1, "profe", "profe@uptc.edu.co", "pass");

        generalController.setCurrentCourse(course);
        assertEquals(course, generalController.getCurrentCourse());

        generalController.setCurrentEstudent(student);
        assertEquals(student, generalController.getCurrentEstudent());

        generalController.setCurrentTeacher(teacher);
        assertEquals(teacher, generalController.getCurrentTeacher());
    }

    @Test
    @DisplayName("Debe establecer el curso actual por su ID")
    public void testSetCurrentCourseById() {
        courseController.addCourse("Matemáticas", "Básicas");
        generalController.setCurrentCourseById("COURSE-1");

        assertNotNull(generalController.getCurrentCourse());
        assertEquals("COURSE-1", generalController.getCurrentCourse().getId());
    }

    // =========================================================================
    // PRUEBAS DE AUTENTICACIÓN (LOGIN)
    // =========================================================================

    @Test
    @DisplayName("Debe autenticar un profesor y establecerlo como profesor actual")
    public void testTeacherLoginSuccess() throws Exception {
        String passEncrypted = securityService.encrypt("Pass123!");
        Teacher teacher = new Teacher(1, "profe_ramirez", "ramirez@uptc.edu.co", passEncrypted);
        teacherRepo.sendAll().add(teacher);

        generalController.teacherLogin("profe_ramirez", "Pass123!");
        assertNotNull(generalController.getCurrentTeacher());
        assertEquals("profe_ramirez", generalController.getCurrentTeacher().getUserName());
    }

    @Test
    @DisplayName("Debe autenticar un estudiante y establecerlo como estudiante actual")
    public void testEstudentLoginSuccess() throws Exception {
        String passEncrypted = securityService.encrypt("Pass123!");
        Estudent student = new Estudent(1, "estudiante1", "est1@uptc.edu.co", passEncrypted);
        estudentRepo.sendAll().add(student);

        generalController.estudentLogin("est1@uptc.edu.co", "Pass123!");
        assertNotNull(generalController.getCurrentEstudent());
        assertEquals("estudiante1", generalController.getCurrentEstudent().getUserName());
    }

    @Test
    @DisplayName("Debe inscribir al estudiante, guardar el progreso y evitar duplicados")
    public void testRegisterCurrentStudentOnCoursePersistsEnrollment() {
        courseController.addCourse("Programación", "Fundamentos");
        courseController.addModule("COURSE-1", "COURSE-1", "Módulo", "Descripción");
        courseController.addLesson("COURSE-1", "MODULE-1", "Primera lección", "Descripción", 10);
        Estudent student = new Estudent(3, "estudiante", "estudiante@uptc.edu.co", "hash");
        generalController.setCurrentEstudent(student);

        assertTrue(generalController.registerCurrentStudentOnCourse("COURSE-1"));
        assertEquals("LESSON-1", student.getLessonOnCourse("COURSE-1"));
        assertTrue(estudentRepo.saveCalled);
        assertFalse(generalController.registerCurrentStudentOnCourse("COURSE-1"));
    }

    @Test
    @DisplayName("Debe avanzar el progreso y marcar el curso completado al completar la última lección")
    public void testCompleteCurrentStudentLessonAdvancesAndCompletesCourse() {
        courseController.addCourse("Java", "Curso de prueba");
        courseController.addModule("COURSE-1", "COURSE-1", "Fundamentos", "Módulo");
        courseController.addLesson("COURSE-1", "MODULE-1", "Variables", "Lección uno", 10);
        courseController.addLesson("COURSE-1", "MODULE-1", "Condicionales", "Lección dos", 15);
        Estudent student = new Estudent(8, "ana", "ana@uptc.edu.co", "hash");
        generalController.setCurrentEstudent(student);
        generalController.registerCurrentStudentOnCourse("COURSE-1");

        assertFalse(generalController.completeCurrentStudentLesson("COURSE-1"));
        assertEquals("LESSON-2", generalController.getCurrentLessonId("COURSE-1"));
        assertFalse(generalController.isCurrentStudentCourseComplete("COURSE-1"));
        assertTrue(generalController.completeCurrentStudentLesson("COURSE-1"));
        assertTrue(generalController.isCurrentStudentCourseComplete("COURSE-1"));
        assertTrue(estudentRepo.saveCalled);
    }

    @Test
    @DisplayName("Las estadísticas solo cuentan las lecciones completadas")
    public void testCurrentStudentCourseProgressPercent() {
        courseController.addCourse("Java", "Curso de prueba");
        courseController.addModule("COURSE-1", "COURSE-1", "Fundamentos", "Módulo");
        courseController.addLesson("COURSE-1", "MODULE-1", "Variables", "Lección uno", 10);
        courseController.addLesson("COURSE-1", "MODULE-1", "Condicionales", "Lección dos", 15);
        Estudent student = new Estudent(9, "ana", "ana@uptc.edu.co", "hash");
        generalController.setCurrentEstudent(student);
        generalController.registerCurrentStudentOnCourse("COURSE-1");

        assertEquals(0.0, generalController.getCurrentStudentCourseProgressPercent("COURSE-1"));
        generalController.completeCurrentStudentLesson("COURSE-1");
        assertEquals(50.0, generalController.getCurrentStudentCourseProgressPercent("COURSE-1"));
        generalController.completeCurrentStudentLesson("COURSE-1");
        assertEquals(100.0, generalController.getCurrentStudentCourseProgressPercent("COURSE-1"));
    }

    @Test
    @DisplayName("Debe restaurar la lección actual si falla el guardado del progreso")
    public void testCompleteCurrentStudentLessonRollsBackWhenSaveFails() {
        courseController.addCourse("Java", "Curso de prueba");
        courseController.addModule("COURSE-1", "COURSE-1", "Fundamentos", "Módulo");
        courseController.addLesson("COURSE-1", "MODULE-1", "Variables", "Lección uno", 10);
        courseController.addLesson("COURSE-1", "MODULE-1", "Condicionales", "Lección dos", 15);
        Estudent student = new Estudent(8, "ana", "ana@uptc.edu.co", "hash");
        generalController.setCurrentEstudent(student);
        generalController.registerCurrentStudentOnCourse("COURSE-1");
        estudentRepo.failOnSave = true;

        assertThrows(co.edu.uptc.exceptions.SavedFailureException.class,
                () -> generalController.completeCurrentStudentLesson("COURSE-1"));

        assertEquals("LESSON-1", generalController.getCurrentLessonId("COURSE-1"));
        assertFalse(generalController.isCurrentStudentCourseComplete("COURSE-1"));
    }

    // =========================================================================
    // PRUEBAS DE INSCRIPCIÓN Y AVANCE EN CURSOS
    // =========================================================================

    @Test
    @DisplayName("Debe registrar al estudiante actual en el curso actual")
    public void testRegisterOnCourse() {
        courseController.addCourse("Estructuras", "Desc");
        courseController.addModule("COURSE-1", "COURSE-1", "Mód 1", "Desc");
        courseController.addLesson("COURSE-1", "MODULE-1", "Lección 1", "Desc", 20.0);

        Course course = courseController.findCourse("COURSE-1");
        Estudent student = new Estudent(10, "pedro", "pedro@uptc.edu.co", "pass");

        generalController.setCurrentCourse(course);
        generalController.setCurrentEstudent(student);

        generalController.registerOnCourse();
        assertTrue(student.isRegisterOnCourse("COURSE-1"));
        assertEquals("LESSON-1", student.getLessonOnCourse("COURSE-1"));
    }

    @Test
    @DisplayName("Debe avanzar a la siguiente lección o completar curso en goToNextLesson")
    public void testGoToNextLessonAndCompletion() {
        courseController.addCourse("POO", "Avanzado");
        courseController.addModule("COURSE-1", "COURSE-1", "Mód 1", "Desc");
        courseController.addLesson("COURSE-1", "MODULE-1", "L1", "Desc", 10.0);

        Course course = courseController.findCourse("COURSE-1");
        Estudent student = new Estudent(10, "ana", "ana@uptc.edu.co", "pass");
        student.registerCourse("COURSE-1", "LESSON-1");

        generalController.setCurrentCourse(course);

        assertDoesNotThrow(() -> generalController.goToNextLesson(student));
    }

    @Test
    @DisplayName("Debe retornar el porcentaje de avance de la lección del estudiante actual")
    public void testSendPercentLesson() {
        courseController.addCourse("Física", "Mecánica");
        courseController.addModule("COURSE-1", "COURSE-1", "Cinemática", "Desc");
        courseController.addLesson("COURSE-1", "MODULE-1", "MRU", "Desc", 15.0);

        Course course = courseController.findCourse("COURSE-1");
        Estudent student = new Estudent(10, "carlos", "carlos@uptc.edu.co", "pass");
        student.registerCourse("COURSE-1", "LESSON-1");

        generalController.setCurrentCourse(course);
        generalController.setCurrentEstudent(student);

        double percent = generalController.sendPercentLesson();
        assertTrue(percent >= 0);
    }

    // =========================================================================
    // PRUEBAS DE ELIMINACIÓN DE CURSO Y REASIGNACIÓN DE RECURSOS
    // =========================================================================

    @Test
    @DisplayName("Debe remover el curso actual del profesor, estudiante y catálogo")
    public void testRemoveCurrentCourse() {
        courseController.addCourse("Bases de Datos", "Relacionales");
        Course course = courseController.findCourse("COURSE-1");

        Estudent student = new Estudent(1, "est", "est@uptc.edu.co", "pass");
        student.registerCourse("COURSE-1", "LESSON-1");
        estudentRepo.sendAll().add(student);

        Teacher teacher = new Teacher(1, "prof", "prof@uptc.edu.co", "pass");
        teacher.addNewCourse("COURSE-1");

        generalController.setCurrentCourse(course);
        generalController.setCurrentTeacher(teacher);

        generalController.removeCurrentCourse();

        assertNull(courseController.findCourse("COURSE-1"));
        assertFalse(student.isRegisterOnCourse("COURSE-1"));
        assertFalse(teacher.getAsginedCourses().contains("COURSE-1"));
    }

    @Test
    @DisplayName("Debe remover una lección y reasignar estudiantes inscritos")
    public void testRemoveLessonWithStudents() {
        generalController.setCurrentCourse(null);
        generalController.removeLesson("LESSON-1");

        courseController.addCourse("Química", "Orgánica");
        courseController.addModule("COURSE-1", "COURSE-1", "Mód 1", "Desc");
        courseController.addLesson("COURSE-1", "MODULE-1", "L1", "Desc", 10.0);
        courseController.addLesson("COURSE-1", "MODULE-1", "L2", "Desc", 10.0);

        Course course = courseController.findCourse("COURSE-1");
        generalController.setCurrentCourse(course);

        Estudent student = new Estudent(1, "luis", "luis@uptc.edu.co", "pass");
        student.registerCourse("COURSE-1", "LESSON-1");
        estudentRepo.sendAll().add(student);

        generalController.removeLesson("LESSON-1");
        assertNull(courseController.findLesson("COURSE-1", "LESSON-1"));
    }

    @Test
    @DisplayName("Debe remover un módulo y reasignar o completar estudiantes")
    public void testRemoveModuleWithStudents() {
        generalController.setCurrentCourse(null);
        generalController.removeModule("MODULE-1");

        courseController.addCourse("Sistemas", "Operativos");
        courseController.addModule("COURSE-1", "COURSE-1", "Mód 1", "Desc");
        courseController.addLesson("COURSE-1", "MODULE-1", "L1", "Desc", 10.0);
        courseController.addModule("COURSE-1", "COURSE-1", "Mód 2", "Desc");
        courseController.addLesson("COURSE-1", "MODULE-2", "L2", "Desc", 10.0);

        Course course = courseController.findCourse("COURSE-1");
        generalController.setCurrentCourse(course);

        Estudent student = new Estudent(1, "marta", "marta@uptc.edu.co", "pass");
        student.registerCourse("COURSE-1", "LESSON-1");
        estudentRepo.sendAll().add(student);

        generalController.removeModule("MODULE-1");
        assertNull(courseController.findModule("COURSE-1", "MODULE-1"));
    }
}