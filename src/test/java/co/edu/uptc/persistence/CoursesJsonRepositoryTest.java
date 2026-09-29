package co.edu.uptc.persistence;

import co.edu.uptc.model.Course;
import co.edu.uptc.interfaces.EducativeElement;
import co.edu.uptc.model.Lessons;
import co.edu.uptc.model.Module;
import co.edu.uptc.model.TreeNode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para {@link CoursesJsonRepository}.
 * Enfocada en verificar las operaciones de guardado (saveAll) y lectura (sendAll).
 * 
 * @author @FELPBEC
 * @version v1.0
 */
public class CoursesJsonRepositoryTest {

    @Test
    @DisplayName("Debe retornar lista vacía si el archivo de cursos no existe")
    void shouldReturnEmptyWhenFileDoesNotExist() throws Exception {
        Path file = Path.of("target", "courses-not-exist.json");
        Files.deleteIfExists(file);

        CoursesJsonRepository repository = new CoursesJsonRepository(file.toString());
        List<Course> courses = repository.sendAll();

        assertNotNull(courses, "La lista retornada no debe ser nula");
        assertTrue(courses.isEmpty(), "La lista debe estar vacía");
    }

    @Test
    @DisplayName("Debe guardar y cargar la lista de cursos mediante saveAll y sendAll")
    void shouldSaveAndLoadCourses() throws Exception {
        Path file = Files.createTempFile("courses-test", ".json");

        try {
            CoursesJsonRepository repository = new CoursesJsonRepository(file.toString());

            List<Course> list = new ArrayList<>();
            
            Course course1 = new Course("1", "Programación II", null);
            course1.setDescription("Curso avanzado de POO");

            Course course2 = new Course("2", "Estructuras de Datos", null);
            course2.setDescription("Algoritmos y Estructuras");

            list.add(course1);
            list.add(course2);

            // Guardar la lista de cursos en el archivo temporal
            repository.saveAll(list);

            // Leer y recuperar los datos persistidos
            List<Course> loadedCourses = repository.sendAll();

            assertEquals(2, loadedCourses.size(), "Debe haber recuperado exactamente 2 cursos");
            
            assertEquals("COURSE-1", loadedCourses.get(0).getId());
            assertEquals("Programación II", loadedCourses.get(0).getTitle());
            assertEquals("Curso avanzado de POO", loadedCourses.get(0).getDescription());

            assertEquals("COURSE-2", loadedCourses.get(1).getId());
            assertEquals("Estructuras de Datos", loadedCourses.get(1).getTitle());

        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    @DisplayName("Debe guardar y restaurar el árbol del curso con módulos y lecciones")
    void shouldSaveAndLoadCourseTree() throws Exception {
        Path file = Files.createTempFile("course-tree-test", ".json");

        try {
            CoursesJsonRepository repository = new CoursesJsonRepository(file.toString());
            Course course = new Course();
            course.setId("7");
            course.setTitle("Programación");
            course.setDescription("Fundamentos");
            TreeNode<EducativeElement> root = new TreeNode<>(course);
            course.setRoot(root);

            Module module = new Module("3", "Variables", "Tipos de datos");
            TreeNode<EducativeElement> moduleNode = new TreeNode<>(module);
            root.addSon(moduleNode);
            moduleNode.addSon(new TreeNode<EducativeElement>(
                    new Lessons("5", "Tipos primitivos", "Descripción", 20)));
            repository.saveAll(List.of(course));

            Course loaded = repository.sendAll().get(0);

            assertEquals("COURSE-7", loaded.getId());
            assertInstanceOf(Course.class, loaded.getRoot().getData());
            assertInstanceOf(Module.class, loaded.getRoot().getSons().get(0).getData());
            assertInstanceOf(Lessons.class,
                    loaded.getRoot().getSons().get(0).getSons().get(0).getData());
            assertEquals("LESSON-5", ((Lessons) loaded.getRoot().getSons().get(0)
                    .getSons().get(0).getData()).getId());
        } finally {
            Files.deleteIfExists(file);
        }
    }
}