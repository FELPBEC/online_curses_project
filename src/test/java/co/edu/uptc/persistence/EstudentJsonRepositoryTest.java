package co.edu.uptc.persistence;

import co.edu.uptc.model.Estudent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para {@link EstudentJsonRepository} usando archivos temporales.
 * 
 * @author @FELPBEC
 * @version v1.0
 */
public class EstudentJsonRepositoryTest {

    @Test
    @DisplayName("Debe retornar una lista vacía cuando el archivo no existe")
    void shouldReturnEmptyWhenFileDoesNotExist() throws Exception {
        Path file = Path.of("target", "estudent-not-exist.json");
        Files.deleteIfExists(file);

        EstudentJsonRepository repository = new EstudentJsonRepository(file.toString());
        List<Estudent> students = repository.sendAll();

        assertNotNull(students);
        assertTrue(students.isEmpty());
    }

    @Test
    @DisplayName("Debe guardar y recuperar la lista de estudiantes")
    void shouldSaveAndLoadEstudents() throws Exception {
        Path file = Files.createTempFile("estudents-test", ".json");

        try {
            EstudentJsonRepository repository = new EstudentJsonRepository(file.toString());

            List<Estudent> students = new ArrayList<>();
            Estudent student = new Estudent(201, "carlos_mora", "carlos@uptc.edu.co", "pass123");
            students.add(student);

            repository.saveAll(students);

            List<Estudent> result = repository.sendAll();
            assertEquals(1, result.size());
            assertEquals(201, result.get(0).getId());
            assertEquals("carlos_mora", result.get(0).getUserName());
        } finally {
            Files.deleteIfExists(file);
        }
    }
}