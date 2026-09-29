package co.edu.uptc.abstracts;

import com.google.gson.reflect.TypeToken;
import co.edu.uptc.exceptions.SavedFailureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para {@link JsonRepository} que cubren persistencia,
 * manejo de archivos no existentes, JSON corrupto y captura de excepciones.
 */
public class JsonRepositoryTest {

    private static class ConcreteJsonRepository extends JsonRepository<String> {
        public ConcreteJsonRepository(String filePath, Type typeClass) {
            super(filePath, typeClass);
        }
    }

    private final Type stringListType = new TypeToken<List<String>>() {}.getType();

    @Test
    @DisplayName("Debe guardar y leer datos correctamente usando una ruta absoluta")
    public void testSaveAndSendAllSuccess(@TempDir Path tempDir) {
        String absolutePath = tempDir.resolve("data_test.json").toString();
        ConcreteJsonRepository repository = new ConcreteJsonRepository(absolutePath, stringListType);

        List<String> items = List.of("Elemento 1", "Elemento 2");
        repository.saveAll(items);

        List<String> loadedItems = repository.sendAll();
        assertEquals(2, loadedItems.size());
        assertEquals("Elemento 1", loadedItems.get(0));
    }

    @Test
    @DisplayName("Debe procesar la inicialización con ruta relativa creando carpeta data si no existe")
    public void testRelativeFilePathConstructor() {
        ConcreteJsonRepository repository = new ConcreteJsonRepository("test_relative.json", stringListType);
        assertNotNull(repository.sendAll());
    }

    @Test
    @DisplayName("Debe retornar lista vacía si el archivo no existe")
    public void testSendAllFileNotFound(@TempDir Path tempDir) {
        String nonExistentPath = tempDir.resolve("not_found.json").toString();
        ConcreteJsonRepository repository = new ConcreteJsonRepository(nonExistentPath, stringListType);

        List<String> result = repository.sendAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe informar un error si el archivo contiene JSON corrupto")
    public void testSendAllCorruptJson(@TempDir Path tempDir) throws IOException {
        File corruptFile = tempDir.resolve("corrupt.json").toFile();
        try (FileWriter writer = new FileWriter(corruptFile)) {
            writer.write("{ json_invalido: ");
        }

        ConcreteJsonRepository repository = new ConcreteJsonRepository(corruptFile.getAbsolutePath(), stringListType);
        assertThrows(SavedFailureException.class, repository::sendAll);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando el archivo contiene JSON nulo")
    public void testSendAllJsonNull(@TempDir Path tempDir) throws IOException {
        File nullFile = tempDir.resolve("null.json").toFile();
        try (FileWriter writer = new FileWriter(nullFile)) {
            writer.write("null");
        }

        ConcreteJsonRepository repository = new ConcreteJsonRepository(nullFile.getAbsolutePath(), stringListType);

        assertTrue(repository.sendAll().isEmpty());
    }

    @Test
    @DisplayName("Debe crear las carpetas necesarias antes de guardar")
    public void testSaveAllCreatesParentDirectories(@TempDir Path tempDir) {
        Path file = tempDir.resolve("nested").resolve("students.json");
        ConcreteJsonRepository repository = new ConcreteJsonRepository(file.toString(), stringListType);

        repository.saveAll(List.of("Estudiante"));

        assertTrue(file.toFile().isFile());
        assertEquals(List.of("Estudiante"), repository.sendAll());
    }

    @Test
    @DisplayName("Debe lanzar SavedFailureException al ocurrir un error de I/O durante la escritura")
    public void testSaveAllThrowsSavedFailureException(@TempDir Path tempDir) {
        // Se apunta a un directorio en lugar de un archivo para provocar un IOException
        File dirAsFile = tempDir.resolve("invalid_dir").toFile();
        dirAsFile.mkdirs();

        ConcreteJsonRepository repository = new ConcreteJsonRepository(dirAsFile.getAbsolutePath(), stringListType);

        assertThrows(SavedFailureException.class, () -> repository.saveAll(List.of("Dato")));
    }
}