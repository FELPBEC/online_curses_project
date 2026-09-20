package co.edu.uptc.model;

import co.edu.uptc.interfaces.EducativeElement;
import co.edu.uptc.interfaces.EducativeElementType;
/**Clase Modulo que implementa la interfaz de {@link EducativeElement} 
 * que funciona como contenedor de las lecciones o de modulos mas pequeños
 * 
 * @author @FELPBEC
 * @version v1.0
 * @since 19/09/2026
 */
public class Module implements EducativeElement{
    private String id;
    private String title;
    private String description;
    
    /**Constructor vacío para cargar modulos desde persistencia
     * 
     */
    public Module() {
    }
    /**Método constructor con párametros para la creación de modulos mediante ingreso de datos
     * 
     * @param id identificador del modulo
     * @param title nombre del modulo
     * @param description descrición del modulo
     */
    public Module(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    /**Método implementado de la intefaz que obtiene el tipo de elemento educativo
     * 
     * @see co.edu.uptc.interfaces.EducativeElement#getElementType()
     * @return tipo de dato educativo: MODULO
     */
    @Override
    public EducativeElementType getElementType() {
        return EducativeElementType.MODULO;
    }
    /**Método para obtener la id del modulo
     * 
     * @return id del modulo
     */
    public String getId() {
        return id;
    }
    /**Método para establecer el id del modulo
     * 
     * @param id id del modulo
     */
    public void setId(int id) {
        this.id = "MODULE-"+id;
    }
    /**Método para obtener el titulo del modulo
     * 
     * @return titutlo del modulo
     */
    public String getTitle() {
        return title;
    }
    /**Método para establecer el título del modulo
     * 
     * @param title titulo del modulo
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**Método para obtener la descripción del modulo
     * 
     * @return descripción del modulo
     */
    public String getDescription() {
        return description;
    }
    /**Método para establecer la descripción del modulo
     * 
     * @param description descripción del modulo
     */
    public void setDescription(String description) {
        this.description = description;
    }

    
    
}
