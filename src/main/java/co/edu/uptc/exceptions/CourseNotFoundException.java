package co.edu.uptc.exceptions;

/**
 * CourseNotFoundException
 */
public class CourseNotFoundException extends RuntimeException{
    public CourseNotFoundException(String message){
        super(message);
    }
}
