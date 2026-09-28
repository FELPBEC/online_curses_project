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
    @DisplayName("Debe retornar lista vacía si el archivo contiene JSON corrupto o deserializa nulo")
    public void testSendAllCorruptJson(@TempDir Path tempDir) throws IOException {
        File corruptFile = tempDir.resolve("corrupt.json").toFile();
        try (FileWriter writer = new FileWriter(corruptFile)) {
            writer.write("{ json_invalido: ");
        }

        ConcreteJsonRepository repository = new ConcreteJsonRepository(corruptFile.getAbsolutePath(), stringListType);
        List<String> result = repository.sendAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
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