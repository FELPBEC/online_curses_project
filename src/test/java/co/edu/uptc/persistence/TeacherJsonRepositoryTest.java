package co.edu.uptc.persistence;

import co.edu.uptc.model.Teacher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TeacherJsonRepositoryTest {

    @Test
    @DisplayName("Debe guardar y recuperar la lista de profesores desde un archivo JSON")
    public void testSaveAndSendAll(@TempDir Path tempDir) {
        Path tempFile = tempDir.resolve("teachers_test.json");
        TeacherJsonRepository repository = new TeacherJsonRepository(tempFile.toString());

        List<Teacher> teachers = new ArrayList<>();
        teachers.add(new Teacher(1, "teacher1", "teacher1@test.com", "pass123"));
        teachers.add(new Teacher(2, "teacher2", "teacher2@test.com", "pass456"));

        // Guardar lista
        assertDoesNotThrow(() -> repository.saveAll(teachers));

        // Recuperar lista
        List<Teacher> loadedTeachers = repository.sendAll();
        assertNotNull(loadedTeachers);
        assertEquals(2, loadedTeachers.size());
        assertEquals("teacher1", loadedTeachers.get(0).getUserName());
        assertEquals("teacher2", loadedTeachers.get(1).getUserName());
    }
}