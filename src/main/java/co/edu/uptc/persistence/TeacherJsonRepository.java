package co.edu.uptc.persistence;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import co.edu.uptc.abstracts.JsonRepository;
import co.edu.uptc.model.Teacher;
/**Clase de repositorio tipo Json para la clase profesores 
 * desciende de la clase {@link JsonRepository}
 * 
 * @author @FELPBEC
 * @version v1.0
 * @since 19/09/2026
 */
public class TeacherJsonRepository extends JsonRepository<Teacher>{
    /**Método constructor de la clase TeacherJsonRepository que inicializa el repositorio
     * 
     * @param filePath ruta del archivo Json
     */
    public TeacherJsonRepository(String filePath) {
        super(filePath, new TypeToken<List<Teacher>>(){}.getType());
    }
    /**Método para guardar una lista de profesores en un archivo Json
     * 
     * @see co.edu.uptc.abstracts.JsonRepository#saveAll(java.util.List)
     * @param objectList lista de profesores
     */
    @Override
    public void saveAll(List<Teacher> objectList) {
        super.saveAll(objectList);
    }
    /**Método para cargar una lista de profesores desde un archivo Json
     * 
     * @see co.edu.uptc.abstracts.JsonRepository#sendAll()
     * @return Lista de profesores
     */
    @Override
    public List<Teacher> sendAll() {
        return super.sendAll();
    }

}

