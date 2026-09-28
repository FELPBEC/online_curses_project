package co.edu.uptc.exceptions;

/**
 * Excepción personalizada lanzada cuando no se encuentra una lección especificada.
 * 
 * @author @jm1407db
 * @version v1.0
 */
public class LessonNotFoundException extends RuntimeException {
    public LessonNotFoundException(String message) {
        super(message);
    }
}