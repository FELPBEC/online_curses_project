package co.edu.uptc.controller;

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
 * Diseñada para obtener máxima cobertura de código en JaCoCo.
 * 
 * @author @jm1407db
 * @version v2.0
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
        courseController.getCourseList().get(0).getTitle();
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
        
        // Eliminar inexistente
        assertFalse(courseController.deleteCourse("COURSE-999"));

        // Eliminar existente
        boolean deleted = courseController.deleteCourse("COURSE-1");
        assertTrue(deleted);
        assertNull(courseController.findCourse("COURSE-1"));
    }

    @Test
    @DisplayName("Debe actualizar título de curso y rechazar títulos nulos o vacíos")
    public void testUpdateTitleCourseValidation() {
        courseController.addCourse("Matemáticas", "Básicas");

        // Curso no existe
        assertFalse(courseController.updateTitleCourse("COURSE-999", "Nuevo Título"));

        // Título nulo o en blanco
        assertFalse(courseController.updateTitleCourse("COURSE-1", null));
        assertFalse(courseController.updateTitleCourse("COURSE-1", "   "));

        // Caso exitoso
        assertTrue(courseController.updateTitleCourse("COURSE-1", "Matemáticas Discretas"));
        assertEquals("Matemáticas Discretas", courseController.findCourse("COURSE-1").getTitle());
    }

    // =========================================================================
    // PRUEBAS DE MÓDULOS (CRUD + ANIDACIÓN + VALIDACIONES)
    // =========================================================================

    @Test
    @DisplayName("Debe agregar módulos y manejar casos donde el curso o padre no existen")
    public void testAddModuleEdgeCases() {
        courseController.addCourse("Estructuras de Datos", "Árboles");

        // Curso inexistente
        courseController.addModule("COURSE-999", "COURSE-999", "Módulo X", "Desc");
        assertNull(courseController.findModule("COURSE-999", "MODULE-1"));

        // Padre inexistente
        courseController.addModule("COURSE-1", "PADRE-INEXISTENTE", "Módulo Y", "Desc");
        assertNull(courseController.findModule("COURSE-1", "MODULE-1"));

        // Agregar módulo bajo la raíz del curso
        courseController.addModule("COURSE-1", "COURSE-1", "Módulo 1", "Desc 1");
        assertNotNull(courseController.findModule("COURSE-1", "MODULE-1"));

        // Agregar submódulo dentro de Módulo 1 (Anidación recursiva)
        courseController.addModule("COURSE-1", "MODULE-1", "Submódulo 1.1", "Desc 1.1");
        assertNotNull(courseController.findModule("COURSE-1", "MODULE-2"));
    }

    @Test
    @DisplayName("Debe retornar null al buscar módulo cuando el curso no existe o el nodo no es módulo")
    public void testFindModuleEdgeCases() {
        courseController.addCourse("Redes", "Telecomunicaciones");
        
        // Curso inexistente
        assertNull(courseController.findModule("COURSE-999", "MODULE-1"));

        // El nodo buscado es el propio Curso (no es de tipo MODULO)
        assertNull(courseController.findModule("COURSE-1", "COURSE-1"));

        // Módulo no existente en el árbol
        assertNull(courseController.findModule("COURSE-1", "MODULE-999"));
    }

    @Test
    @DisplayName("Debe eliminar un módulo y retornar false si no existe el curso o el módulo")
    public void testDeleteModuleEdgeCases() {
        courseController.addCourse("Bases de Datos", "Relacionales");
        courseController.addModule("COURSE-1", "COURSE-1", "Módulo SQL", "Consultas");

        // Curso no existe
        assertFalse(courseController.deleteModule("COURSE-999", "MODULE-1"));

        // Módulo no existe
        assertFalse(courseController.deleteModule("COURSE-1", "MODULE-999"));

        // Eliminación exitosa
        assertTrue(courseController.deleteModule("COURSE-1", "MODULE-1"));
        assertNull(courseController.findModule("COURSE-1", "MODULE-1"));
    }

    @Test
    @DisplayName("Debe actualizar módulo y manejar campos nulos/vacíos conservando valores")
    public void testUpdateModuleValidation() {
        courseController.addCourse("Redes", "Avanzadas");
        courseController.addModule("COURSE-1", "COURSE-1", "IPv4", "Direccionamiento");

        // Módulo inexistente
        assertFalse(courseController.updateModule("COURSE-1", "MODULE-999", "Nuevo", "Desc"));

        // Actualizar enviando null/blank para mantener valores anteriores
        assertTrue(courseController.updateModule("COURSE-1", "MODULE-1", "   ", "Nueva Desc"));
        Module module = courseController.findModule("COURSE-1", "MODULE-1");
        assertEquals("IPv4", module.getTitle()); // Mantiene el título original
        assertEquals("Nueva Desc", module.getDescription());
    }

    // =========================================================================
    // PRUEBAS DE LECCIONES (CRUD + VALIDACIONES DE PADRE)
    // =========================================================================

    @Test
    @DisplayName("Debe validar que las lecciones solo se agreguen a Módulos válidos")
    public void testAddLessonEdgeCases() {
        courseController.addCourse("Física", "Mecánica");
        courseController.addModule("COURSE-1", "COURSE-1", "Cinemática", "Movimiento");

        // Curso no existe
        courseController.addLesson("COURSE-999", "MODULE-1", "L1", "Desc", 30.0);

        // Padre no existe
        courseController.addLesson("COURSE-1", "PADRE-INEXISTENTE", "L1", "Desc", 30.0);

        // Intentar agregar una lección directamente al Curso (Padre es un Curso, no un Módulo)
        courseController.addLesson("COURSE-1", "COURSE-1", "L1", "Desc", 30.0);
        assertNull(courseController.findLesson("COURSE-1", "LESSON-1"));

        // Agregar lección correctamente al Módulo
        courseController.addLesson("COURSE-1", "MODULE-1", "Lección 1", "Desc", 45.0);
        assertNotNull(courseController.findLesson("COURSE-1", "LESSON-1"));
    }

    @Test
    @DisplayName("Debe retornar null al buscar lección en curso inexistente o tipo incorrecto")
    public void testFindLessonEdgeCases() {
        courseController.addCourse("Química", "General");
        courseController.addModule("COURSE-1", "COURSE-1", "Atomos", "Estructura");

        // Curso inexistente
        assertNull(courseController.findLesson("COURSE-999", "LESSON-1"));

        // El ID solicitado pertenece a un Módulo, no a una Lección
        assertNull(courseController.findLesson("COURSE-1", "MODULE-1"));

        // Lección inexistente
        assertNull(courseController.findLesson("COURSE-1", "LESSON-999"));
    }

    @Test
    @DisplayName("Debe eliminar lección y responder false cuando no existe curso o lección")
    public void testDeleteLessonEdgeCases() {
        courseController.addCourse("Historia", "Universal");
        courseController.addModule("COURSE-1", "COURSE-1", "Módulo 1", "Desc");
        courseController.addLesson("COURSE-1", "MODULE-1", "L1", "Desc", 20.0);

        // Curso no existe
        assertFalse(courseController.deleteLesson("COURSE-999", "LESSON-1"));

        // Lección no existe
        assertFalse(courseController.deleteLesson("COURSE-1", "LESSON-999"));

        // Eliminación exitosa
        assertTrue(courseController.deleteLesson("COURSE-1", "LESSON-1"));
        assertNull(courseController.findLesson("COURSE-1", "LESSON-1"));
    }

    @Test
    @DisplayName("Debe actualizar lección validando duración > 0 y campos opcionales")
    public void testUpdateLessonValidation() {
        courseController.addCourse("Arte", "Pintura");
        courseController.addModule("COURSE-1", "COURSE-1", "Óleo", "Técnicas");
        courseController.addLesson("COURSE-1", "MODULE-1", "Pinceles", "Tipos", 15.0);

        // Lección no existe
        assertFalse(courseController.updateLesson("COURSE-1", "LESSON-999", "T", "D", 10.0));

        // Actualizar con duración <= 0 (no debe cambiar la duración)
        assertTrue(courseController.updateLesson("COURSE-1", "LESSON-1", "Pinceles Finos", null, -5.0));
        Lessons lesson = courseController.findLesson("COURSE-1", "LESSON-1");
        assertEquals("Pinceles Finos", lesson.getTitle());
        assertEquals("Tipos", lesson.getDescription()); // Se mantuvo
        assertEquals(15.0, lesson.getDuration());       // Se mantuvo
    }

    // =========================================================================
    // PRUEBAS DE RECORRIDO PREORDEN Y PERSISTENCIA
    // =========================================================================

    @Test
    @DisplayName("Debe retornar lista vacía al solicitar PreOrden de un curso inexistente o sin raíz")
    public void testGetPreOrderEdgeCases() {
        assertTrue(courseController.getPreOrder("COURSE-999").isEmpty());

        // Curso sin raíz
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

    // =========================================================================
    // PRUEBAS PARA CUBRIR BLOQUES CATCH Y FORMATOS ANÓMALOS DE ID
    // =========================================================================

    @Test
    @DisplayName("Debe manejar IDs con formato no numérico o sin guión en getNextCourseIdNumber y findMaxId")
    public void testMalformedIdsAndExceptions() {
        // 1. Crear un curso manualmente con un ID sin formato adecuado
        Course badCourse = new Course();
        badCourse.setId("ID_SIN_NUMERO");
        badCourse.setTitle("Curso Malformado");

        // Nodo con ID no numérico para forzar el catch en findMaxIdRecursive
        Module badModule = new Module();
        badModule.setId("MODULE-TEXTO_NO_NUMERICO");
        TreeNode<EducativeElement> root = new TreeNode<>(badCourse);
        root.addSon(new TreeNode<>(badModule));
        badCourse.setRoot(root);

        dummyRepository.sendAll().add(badCourse);

        // Al agregar un nuevo curso o módulo, debe omitir el ID corrupto sin lanzar excepción
        assertDoesNotThrow(() -> courseController.addCourse("Nuevo Curso", "Desc"));
        assertDoesNotThrow(() -> courseController.addModule("COURSE-1", "COURSE-1", "Nuevo Mod", "Desc"));
    }
}