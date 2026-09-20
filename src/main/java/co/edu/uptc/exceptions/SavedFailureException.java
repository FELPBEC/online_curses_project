package co.edu.uptc.exceptions;


/**Excepción personalizada cuando no se puede guardar en persistencia
 * 
 * @author @FELPBEC
 * @version v1.0
 * @since 20/09/2026
 */
public class SavedFailureException extends RuntimeException{
    public SavedFailureException(String message, Throwable cause){
        super(message,cause);
    }

}
