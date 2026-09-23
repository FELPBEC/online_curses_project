package co.edu.uptc.exceptions;

/**Excepción personalizada cuando no se encuentra a un estudiante
 * 
 * @author @FELPBEC
 * @version v1.0
 * @since 20/09/2026
 */
public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message){
        super(message);
    }
}
