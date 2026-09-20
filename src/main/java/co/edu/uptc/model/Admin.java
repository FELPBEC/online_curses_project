package co.edu.uptc.model;
import co.edu.uptc.abstracts.User;
/**Clase que representa a los administradores que desciende de la clase {@link User} 
 * 
 * @author @FELPBEC
 * @version v1.0
 * @since 19/09/2026
 */
public class Admin extends User{
    /**Constructor vacío de la clase admin para cargar desde persistencia
     * 
     */
    public Admin() {
    }
    /**Método constructor con párametros para crear un administrador mediante ingreso de datos
     * 
     * @param userName nombre de usuario del administrador
     * @param email correo electronico del administrador
     * @param password contraseña del administrador
     * @param id    identificador del administrador
     */
    public Admin(String userName, String email, String password, int id) {
        super(userName, email, password, id);
    }
    
}
