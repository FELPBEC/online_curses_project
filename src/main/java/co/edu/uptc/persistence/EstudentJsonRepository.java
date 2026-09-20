package co.edu.uptc.persistence;

import java.util.List;
import com.google.gson.reflect.TypeToken;
import co.edu.uptc.abstracts.JsonRepository;
import co.edu.uptc.model.Estudent;
/**Clase de repositorio tipo Json para la clase estudiantes 
 * desciende de la clase {@link JsonRepository}
 * 
 * @author @FELPBEC
 * @version v1.0
 * @since 19/09/2026
 */
public class EstudentJsonRepository extends JsonRepository<Estudent>{
    /**Método constructor de la clase EstudentJsonRepository
     * 
     * @param filePath la ruta donde se guardaran los estudiantes
     */
    public EstudentJsonRepository(String filePath) {
        super(filePath,  new TypeToken<List<Estudent>>(){}.getType());
    }
    /**Método de guardar lista de estudiantes heredado de la clase JsonRepository
     * 
     * @see co.edu.uptc.abstracts.JsonRepository#saveAll(java.util.List)
     * @param objectList lista de estudiantes
     */
    @Override
    public void saveAll(List<Estudent> objectList) {
        super.saveAll(objectList);
    }
    /** Método para enviar la lista de estudiantes
     * 
     * @see co.edu.uptc.abstracts.JsonRepository#sendAll()
     * @return lista de estudiantes
     */
    @Override
    public List<Estudent> sendAll() {
        return super.sendAll();
    }
    
}
