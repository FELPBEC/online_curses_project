package co.edu.uptc.model;


import co.edu.uptc.interfaces.EducativeElement;
import co.edu.uptc.interfaces.EducativeElementType;

/**Clase de Lecciones que implementa la interfaz de {@link EducativeElement}
 * Contiene una ID, un nombre 
 * y una duración
 * 
 * @author @FELPBEC
 * @version v1.0
 * @since 19/09/2026
 */
public class Lessons implements EducativeElement{
    private String id;
    private String title;
    private String description;
    private double duration;

    /**Método constructor vacío de la clase Lecciones para cargar desde Persistencia
     * 
     */
    public Lessons() {
    }

    /**Método constructor de la clase Lecciones con parametros 
     * para la creación de lecciones con ingreso de datos
     * 
     * @param id identificador númerico de la lección
     * @param title  nombre de la lección
     * @param duration duración de la lección
     */
    public Lessons(String id, String title,String description, double duration) {
        this.id = id;
        this.description=description;
        this.title = title;
        this.duration= duration;
    }
    /**Método implementado de la intefaz que obtiene el tipo de elemento educativo
     * 
     * @see co.edu.uptc.interfaces.EducativeElement#getElementType()
     * @return tipo de dato educativo: LESSON
     */
    @Override
    public EducativeElementType getElementType() {
        
        return EducativeElementType.LESSON;
    }
    /**Método para obtener el identificador de la lección
     * 
     * @return identificador de la lección
     */
    public String getId() {
        return id;
    }
    /**Método para establecer el identificador de la lección
     * 
     * @param id identificador de la lección
     */
    public void setId(int id) {
        this.id ="LESSON-"+id;
    }
    /**Método para obtener el titulo de la lección
     * 
     * @return titulo de la lección
     */
    public String getTitle() {
        return title;
    }
    /**Método para establecer el titulo de la lección
     * 
     * @param title titulo de la lección
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**Método para obtener la duración de la lección
     * 
     * @return la duración de la lección
     */

    public double getDuration() {
        return duration;
    }
    /**Método para establecer la duración de la lección
     * 
     * @param duration la duración de la lección 
     */
    public void setDuration(double duration) {
        this.duration = duration;
    }
    /**Método para obtener la descripción de la lección 
     * 
     * @return descripción de la lección
     */
    public String getDescription() {
        return description;
    }
    /**Método para establecer la descripción de la lección
     * 
     * @param description descripción de la lección
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    
    
}
