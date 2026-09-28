package co.edu.uptc.exceptions;

/**
 * Excepción personalizada lanzada cuando no se encuentra un módulo especificado.
 * 
 * @author @jm1407db
 * @version v1.0
 */
public class ModuleNotFoundException extends RuntimeException {
    public ModuleNotFoundException(String message) {
        super(message);
    }
}