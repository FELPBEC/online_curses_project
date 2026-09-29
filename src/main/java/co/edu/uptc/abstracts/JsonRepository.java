package co.edu.uptc.abstracts;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import co.edu.uptc.exceptions.SavedFailureException;
import co.edu.uptc.interfaces.Repository;

/**Clase abstracta JsonRepository que implementa la interfaz {@link Repository} 
 * que implementa la persistencia para archivos de tipo Json
 * 
 * 
 * @param <T> Tipo generico, ya que permite que se almacene el tipo de dato que se quiera
 */
public abstract class JsonRepository<T> implements Repository<T>{
    private String filePath;
    private Gson gson;
    private Type typeClass;

    /**
     * Constructor de la clase JsonRepository.
     * 
     * @param filePath Nombre de archivo relativo o ruta absoluta del archivo JSON.
     * @param typeClass Tipo de dato o token reflejado para la deserialización.
     */
    public JsonRepository(String filePath, Type typeClass) {
        this(filePath, typeClass, new GsonBuilder().setPrettyPrinting().create());
    }

    public JsonRepository(String filePath, Type typeClass, Gson gson) {
        File fileInput = new File(filePath);

        // Si la ruta recibida ya es absoluta (ej: rutas temporales de JUnit @TempDir)
        if (fileInput.isAbsolute()) {
            this.filePath = fileInput.getAbsolutePath();
        } else {
            // Si es un nombre de archivo relativo (ej: "Courses.json"), se aloja en ../data
            String rutaDeEjecucion = System.getProperty("user.dir");
            File carpetaData = new File(rutaDeEjecucion, "../data");
            File archivoFinal = new File(carpetaData, filePath);
            this.filePath = archivoFinal.getAbsolutePath();
        }

        this.typeClass = typeClass;
        this.gson = gson;
    }
    /**Método guardar todo que actualiza el archivo Json con la lista actual en memoria    
     * 
     * 
     * @see co.edu.uptc.interfaces.Repository#saveAll(java.util.List)
     */
    @Override
    public void saveAll(List<T> objectList) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
            throw new SavedFailureException(
                    "ERROR al crear la carpeta de persistencia " + parent.getAbsolutePath(),
                    new IOException("No fue posible crear la carpeta de persistencia"));
        }
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(objectList, writer);
        } catch (IOException e) {
            throw new SavedFailureException("ERROR al guardar en el archivo " + filePath, e);
        }
    }

    @Override
    public List<T> sendAll() {
        File file= new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(file)) {
            List<T> data = gson.fromJson(reader, typeClass);
            return data != null ? data : new ArrayList<>();
        } catch (IOException | JsonParseException e) {
            throw new SavedFailureException("ERROR al leer el archivo " + filePath, e);
        }
    }
    
}
