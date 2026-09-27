package co.edu.uptc.persistence;

import co.edu.uptc.model.Course;

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
}