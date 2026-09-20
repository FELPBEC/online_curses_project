package co.edu.uptc.persistence;

import java.util.List;
import com.google.gson.reflect.TypeToken;
import co.edu.uptc.abstracts.JsonRepository;
import co.edu.uptc.model.Course;

/**Clase de repositorio de cursos para archivos tipo Json
 * desciende de la clase {@link JsonRepository} 
 * 
 * @author @FELPBEC
 * @version v1.0
 * @since 19/09/2026
 */
public class CoursesJsonRepository extends JsonRepository<Course>{
    /**Método constructor de la clase CoursesJsonRepository
     * 
     * @param filePath el nombre del archivo donde se guardaran los cursos
     */
    public CoursesJsonRepository(String filePath) {
        super(filePath, new TypeToken<List<Course>>(){}.getType());
    }
    /**Método para guardar la lista de cursos en un archivo Json
     * 
     * @see co.edu.uptc.abstracts.JsonRepository#saveAll(java.util.List)
     * @param objectList lista de cursos para guardar
     */
    @Override
    public void saveAll(List<Course> objectList) {
        super.saveAll(objectList);
    }
    /**Método para cargar una lista de cursos desde un archivo Json
     * 
     * @see co.edu.uptc.abstracts.JsonRepository#sendAll()
     * @return lista de cursos del archivo Json
     */

    @Override
    public List<Course> sendAll() {
        return super.sendAll();
    }
    
    
}
