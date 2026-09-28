package co.edu.uptc.exceptions;

/**Excepción personalizada en caso de que se supere el número de lecciones disponibles en un curso
 * Es especialmente útil ya que al ser lanzada determinara que el usuario ya ha completado todas las lecciones del curso
 * 
 * @author @FELPBEC
 * @version v1.0
 * @since 26/09/2026
 * 
 */
public class NoAvaliableLessonsInTheCourseException extends RuntimeException{
    public NoAvaliableLessonsInTheCourseException(String message){
        super(message);
    }
}
