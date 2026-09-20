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

    /**Método constructor de la clase JsonRepository
     * 
     * @param filePath nombre del archivo tipo Json
     * @param typeClass tipo de objeto que se almacenará en ese archivo
     */
    public JsonRepository(String filePath, Type typeClass) {

        String rutaDeEjecucion = System.getProperty("user.dir");
        File carpetaData = new File(rutaDeEjecucion, "../data");
        // Si la carpeta "data" no existe junto al programa, la crea
        if (!carpetaData.exists()) {
            carpetaData.mkdirs(); 
        }
        // Une la carpeta "data" con el nombre del archivo (ej: "Admin.json")
        File archivoFinal = new File(carpetaData, filePath);
        // Guardamos la ruta completa en la variable que usan los demás métodos
        this.filePath = archivoFinal.getAbsolutePath();
        this.typeClass = typeClass;
        this.gson= new GsonBuilder().setPrettyPrinting().create();
    }
    /**Método guardar todo que actualiza el archivo Json con la lista actual en memoria    
     * 
     * 
     * @see co.edu.uptc.interfaces.Repository#saveAll(java.util.List)
     */
    @Override
    public void saveAll(List<T> objectList) {
        List<T> data= objectList;
        try(FileWriter writer= new FileWriter(filePath)) {
            gson.toJson(data, writer); 
        } catch (IOException e) {
            throw new SavedFailureException("ERROR al guardar en el archivo ", e);
        }
        
    }

    @Override
    public List<T> sendAll() {
        File file= new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try(FileReader reader= new FileReader(filePath)) {
            List<T> data =gson.fromJson(reader,typeClass);
            return  data !=null ?   data:  new ArrayList<>();
        } catch (Exception e) {
           return new  ArrayList<>();
        }
    }
    
}
