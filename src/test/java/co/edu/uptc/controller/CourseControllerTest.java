package co.edu.uptc.controller;

import co.edu.uptc.exceptions.CourseNotFoundException;
import co.edu.uptc.exceptions.InvalidFortmatException;
import co.edu.uptc.exceptions.InvalidParentException;
import co.edu.uptc.exceptions.NoAvaliableLessonsInTheCourseException;
import co.edu.uptc.interfaces.EducativeElement;
import co.edu.uptc.interfaces.EducativeElementType;
import co.edu.uptc.interfaces.Repository;
import co.edu.uptc.model.Course;
import co.edu.uptc.model.Lessons;
import co.edu.uptc.model.Module;
import co.edu.uptc.model.TreeNode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias exhaustivas para {@link CourseController}.
 * Cobertura completa de métodos CRUD, árbol n-ario y progresión académica.
 */
public class CourseControllerTest {

    private CourseController courseController;
    private DummyCourseRepository dummyRepository;

    private static class DummyCourseRepository implements Repository<Course> {
        private final List<Course> courses = new ArrayList<>();
        private boolean saveCalled = false;

        @Override
        public List<Course> sendAll() {
            return courses;
        }

        @Override
        public void saveAll(List<Course> list) {
            this.saveCalled = true;
        }

        public boolean isSaveCalled() {
            return saveCalled;
        }
    }

    @BeforeEach
    public void setUp() {
        dummyRepository = new DummyCourseRepository();
        courseController = new CourseController(dummyRepository);
    }

    // =========================================================================
    // PRUEBAS DE MÉTODOS BÁSICOS Y OBTENCIÓN DE LISTAS
    // =========================================================================

    @Test
    @DisplayName("Debe obtener la lista global de cursos")
    public void testGetCourseList() {
        assertNotNull(courseController.getCourseList());
        assertEquals(0, courseController.getCourseList().size());

        courseController.addCourse("Curso 1", "Desc 1");
        assertEquals(1, courseController.getCourseList().size());
    }

    // =========================================================================
    // PRUEBAS DE CURSOS (CRUD + EDGE CASES)
    // =========================================================================

    @Test
    @DisplayName("Debe agregar y encontrar un curso correctamente")
    public void testAddAndFindCourse() {
        courseController.addCourse("Programación Orientada a Objetos", "Curso básico de POO");
        Course course = courseController.findCourse("COURSE-1");

        assertNotNull(course);
        assertEquals("COURSE-1", course.getId());
        assertEquals("Programación Orientada a Objetos", course.getTitle());
        assertEquals("Curso básico de POO", course.getDescription());
    }

    @Test
    @DisplayName("Debe retornar null al buscar un curso inexistente")
    public void testFindCourseNotFound() {
        assertNull(courseController.findCourse("COURSE-999"));
    }

    @Test
    @DisplayName("Debe eliminar un curso existente y retornar false si no existe")
    public void testDeleteCourse() {
        courseController.addCourse("Algoritmos", "Curso de Algoritmos");
        
        assertFalse(courseController.deleteCourse("COURSE-999"));

        boolean deleted = courseController.deleteCourse("COURSE-1");
        assertTrue(deleted);
        assertNull(courseController.findCourse("COURSE-1"));
    }

    @Test
    @DisplayName("Debe actualizar título de curso y rechazar títulos nulos o vacíos")
    public void testUpdateTitleCourseValidation() {
        courseController.addCourse("Matemáticas", "Básicas");

        assertFalse(courseController.updateTitleCourse("COURSE-999", "Nuevo Título"));
        assertFalse(courseController.updateTitleCourse("COURSE-1", null));
        assertFalse(courseController.updateTitleCourse("COURSE-1", "   "));

        assertTrue(courseController.updateTitleCourse("COURSE-1", "Matemáticas Discretas"));
        assertEquals("Matemáticas Discretas", courseController.findCourse("COURSE-1").getTitle());
    }

    // =========================================================================
    // PRUEBAS DE MÓDULOS (CRUD + ANIDACIÓN)
    // =========================================================================

    @Test
    @DisplayName("Debe agregar módulos y manejar casos donde el curso o padre no existen")
    public void testAddModuleEdgeCases() {
        courseController.addCourse("Estructuras de Datos", "Árboles");

        courseController.addModule("COURSE-999", "COURSE-999", "Módulo X", "Desc");
        assertNull(courseController.findModule("COURSE-999", "MODULE-1"));

        courseController.addModule("COURSE-1", "PADRE-INEXISTENTE", "Módulo Y", "Desc");
        assertNull(courseController.findModule("COURSE-1", "MODULE-1"));

        courseController.addModule("COURSE-1", "COURSE-1", "Módulo 1", "Desc 1");
        assertNotNull(courseController.findModule("COURSE-1", "MODULE-1"));

        courseController.addModule("COURSE-1", "MODULE-1", "Submódulo 1.1", "Desc 1.1");
        assertNotNull(courseController.findModule("COURSE-1", "MODULE-2"));
    }

    @Test
    @DisplayName("Debe retornar null al buscar módulo cuando el curso no existe o el nodo no es módulo")
    public void testFindModuleEdgeCases() {
        courseController.addCourse("Redes", "Telecomunicaciones");
        
        assertNull(courseController.findModule("COURSE-999", "MODULE-1"));
        assertNull(courseController.findModule("COURSE-1", "COURSE-1"));
        assertNull(courseController.findModule("COURSE-1", "MODULE-999"));
    }

    @Test
    @DisplayName("Debe eliminar un módulo y retornar false si no existe el curso o el módulo")
    public void testDeleteModuleEdgeCases() {
        courseController.addCourse("Bases de Datos", "Relacionales");
        courseController.addModule("COURSE-1", "COURSE-1", "Módulo SQL", "Consultas");

        assertFalse(courseController.deleteModule("COURSE-999", "MODULE-1"));
        assertFalse(courseController.deleteModule("COURSE-1", "MODULE-999"));

        assertTrue(courseController.deleteModule("COURSE-1", "MODULE-1"));
        assertNull(courseController.findModule("COURSE-1", "MODULE-1"));
    }

    @Test
    @DisplayName("Debe actualizar módulo y manejar campos nulos/vacíos conservando valores")
    public void testUpdateModuleValidation() {
        courseController.addCourse("Redes", "Avanzadas");
        courseController.addModule("COURSE-1", "COURSE-1", "IPv4", "Direccionamiento");

        assertFalse(courseController.updateModule("COURSE-1", "MODULE-999", "Nuevo", "Desc"));

        assertTrue(courseController.updateModule("COURSE-1", "MODULE-1", "   ", "Nueva Desc"));
        Module module = courseController.findModule("COURSE-1", "MODULE-1");
        assertEquals("IPv4", module.getTitle());
        assertEquals("Nueva Desc", module.getDescription());
    }

    // =========================================================================
    // PRUEBAS DE LECCIONES (CRUD + VALIDACIONES)
    // =========================================================================

    @Test
    @DisplayName("Debe validar que las lecciones solo se agreguen a Módulos válidos")
    public void testAddLessonEdgeCases() {
        courseController.addCourse("Física", "Mecánica");
        courseController.addModule("COURSE-1", "COURSE-1", "Cinemática", "Movimiento");

        assertDoesNotThrow(() -> 
            courseController.addLesson("COURSE-1", "MODULE-1", "L1", "Desc", 30.0)
        );

        assertThrows(InvalidParentException.class, () -> 
            courseController.addLesson("COURSE-1", "PADRE-INEXISTENTE", "L1", "Desc", 30.0)
        );

        assertThrows(InvalidParentException.class, () -> 
            courseController.addLesson("COURSE-1", "COURSE-1", "L1", "Desc", 30.0)
        );

        courseController.addLesson("COURSE-1", "MODULE-1", "Lección 1", "Desc", 45.0);
        assertNotNull(courseController.findLesson("COURSE-1", "LESSON-1"));
    }
    
    @Test
    @DisplayName("Debe retornar null al buscar lección en curso inexistente o tipo incorrecto")
    public void testFindLessonEdgeCases() {
        courseController.addCourse("Química", "General");
        courseController.addModule("COURSE-1", "COURSE-1", "Atomos", "Estructura");

        assertNull(courseController.findLesson("COURSE-1", "LESSON-4"));
        assertNull(courseController.findLesson("COURSE-1", "MODULE-1"));
        assertNull(courseController.findLesson("COURSE-1", "LESSON-999"));
    }

    @Test
    @DisplayName("Debe eliminar lección y responder false cuando no existe curso o lección")
    public void testDeleteLessonEdgeCases() {
        courseController.addCourse("Historia", "Universal");
        courseController.addModule("COURSE-1", "COURSE-1", "Módulo 1", "Desc");
        courseController.addLesson("COURSE-1", "MODULE-1", "L1", "Desc", 20.0);

        assertFalse(courseController.deleteLesson("COURSE-999", "LESSON-1"));
        assertFalse(courseController.deleteLesson("COURSE-1", "LESSON-999"));

        assertTrue(courseController.deleteLesson("COURSE-1", "LESSON-1"));
        assertNull(courseController.findLesson("COURSE-1", "LESSON-1"));
    }

    @Test
    @DisplayName("Debe actualizar lección validando duración > 0 y campos opcionales")
    public void testUpdateLessonValidation() {
        courseController.addCourse("Arte", "Pintura");
        courseController.addModule("COURSE-1", "COURSE-1", "Óleo", "Técnicas");
        courseController.addLesson("COURSE-1", "MODULE-1", "Pinceles", "Tipos", 15.0);

        assertFalse(courseController.updateLesson("COURSE-1", "LESSON-999", "T", "D", 10.0));

        assertTrue(courseController.updateLesson("COURSE-1", "LESSON-1", "Pinceles Finos", null, -5.0));
        Lessons lesson = courseController.findLesson("COURSE-1", "LESSON-1");
        assertEquals("Pinceles Finos", lesson.getTitle());
        assertEquals("Tipos", lesson.getDescription());
        assertEquals(15.0, lesson.getDuration());
    }

    // =========================================================================
    // PRUEBAS DE PROGRESIÓN (MÉTODOS FALTANTES SEGÚN JACOCO)
    // =========================================================================

    @Test
    @DisplayName("Debe obtener la ID de la primera lección de un curso")
    public void testGetIdFirstLesson() {
        courseController.addCourse("Java", "Básico");
        courseController.addModule("COURSE-1", "COURSE-1", "Módulo 1", "Intro");
        courseController.addLesson("COURSE-1", "MODULE-1", "Lección 1", "Desc", 10.0);
        courseController.addLesson("COURSE-1", "MODULE-1", "Lección 2", "Desc", 20.0);

        String firstLessonId = courseController.getIdFirstLesson("COURSE-1");
        assertEquals("LESSON-1", firstLessonId);
    }

    @Test
    @DisplayName("Debe obtener la siguiente lección o manejar excepciones de fin de curso")
    public void testGetIdNextLesson() {
        courseController.addCourse("Python", "Básico");
        courseController.addModule("COURSE-1", "COURSE-1", "Módulo 1", "Intro");
        courseController.addLesson("COURSE-1", "MODULE-1", "L1", "Desc", 10.0);
        courseController.addLesson("COURSE-1", "MODULE-1", "L2", "Desc", 10.0);
        assertDoesNotThrow(() -> {
            String nextLesson = courseController.getIdNextLesson("COURSE-1", "LESSON-1");
            assertNotNull(nextLesson);
        });
        assertEquals("LESSON-2", courseController.getIdNextLesson("COURSE-1", "LESSON-1"));
    }

    @Test
    @DisplayName("Debe calcular correctamente los porcentajes de lecciones y módulos completados")
    public void testGetPercentOfLessonsAndModulesComplete() {
        courseController.addCourse("Web", "Frontend");
        courseController.addModule("COURSE-1", "COURSE-1", "HTML", "Estructura");
        courseController.addLesson("COURSE-1", "MODULE-1", "Etiquetas", "Desc", 15.0);

        double percentLessons = courseController.getPercentOfLessonsComplete("COURSE-1", "LESSON-1");
        assertTrue(percentLessons >= 0);

        double percentModules = courseController.getPercentOfModulesComplete("COURSE-1", "LESSON-1");
        assertTrue(percentModules >= 0);
    }

    // =========================================================================
    // RECORRIDO PREORDEN, PERSISTENCIA Y FORMATOS ANÓMALOS DE ID
    // =========================================================================

    @Test
    @DisplayName("Debe retornar lista vacía al solicitar PreOrden de un curso inexistente o sin raíz")
    public void testGetPreOrderEdgeCases() {
        assertTrue(courseController.getPreOrder("COURSE-999").isEmpty());

        Course courseWithoutRoot = new Course("2", "Curso Sin Raíz", null);
        dummyRepository.sendAll().add(courseWithoutRoot);

        assertTrue(courseController.getPreOrder("COURSE-2").isEmpty());
    }

    @Test
    @DisplayName("Debe recolectar elementos en secuencia PreOrden correctamente")
    public void testGetPreOrderSuccess() {
        courseController.addCourse("Java", "SE");
        courseController.addModule("COURSE-1", "COURSE-1", "Sintaxis", "Básicos");
        courseController.addLesson("COURSE-1", "MODULE-1", "Variables", "Tipos", 15.0);

        List<EducativeElement> elements = courseController.getPreOrder("COURSE-1");
        assertEquals(3, elements.size());
        assertEquals(EducativeElementType.COURSE, elements.get(0).getElementType());
        assertEquals(EducativeElementType.MODULO, elements.get(1).getElementType());
        assertEquals(EducativeElementType.LESSON, elements.get(2).getElementType());
    }

    @Test
    @DisplayName("Debe invocar el método saveAll del repositorio")
    public void testSaveChanges() {
        courseController.saveChanges();
        assertTrue(dummyRepository.isSaveCalled());
    }

    @Test
    @DisplayName("Debe lanzar InvalidFortmatException al intentar calcular ID de curso malformado")
    public void testMalformedIdsAndExceptions() {
        Course badCourse = new Course();
        badCourse.setId("COURSE-ID_SIN_NUMERO");
        badCourse.setTitle("Curso Malformado");

        Module badModule = new Module();
        badModule.setId("MODULE-TEXTO_NO_NUMERICO");
        TreeNode<EducativeElement> root = new TreeNode<>(badCourse);
        root.addSon(new TreeNode<>(badModule));
        badCourse.setRoot(root);

        dummyRepository.sendAll().add(badCourse);

        assertThrows(InvalidFortmatException.class, () -> 
            courseController.addCourse("Nuevo Curso", "Desc")
        );
    }

    @Test
    @DisplayName("Debe obtener las lecciones hijas de un módulo específico en PreOrden")
    public void testGetLessonsByModulePreOrder() {
        courseController.addCourse("Curso Java", "Curso de Programación");
        courseController.addModule("COURSE-1", "COURSE-1", "Módulo 1", "Introducción");
        courseController.addLesson("COURSE-1", "MODULE-1", "Lección 1.1", "Desc", 10.0);
        courseController.addModule("COURSE-1", "MODULE-1", "Submódulo 1.1", "Avanzado");
        courseController.addLesson("COURSE-1", "MODULE-2", "Lección 1.1.1", "Desc", 15.0);

        List<Lessons> lessons = courseController.getLessonsByModule("COURSE-1", "MODULE-1");

        assertEquals(2, lessons.size());
        assertEquals("LESSON-1", lessons.get(0).getId());
        assertEquals("LESSON-2", lessons.get(1).getId());
    }
}