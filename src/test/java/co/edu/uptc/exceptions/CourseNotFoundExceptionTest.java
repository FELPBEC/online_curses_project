package co.edu.uptc.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CourseNotFoundExceptionTest {

    @Test
    @DisplayName("Debe instanciar CourseNotFoundException con el mensaje correcto")
    public void testCourseNotFoundExceptionMessage() {
        String message = "Curso no encontrado";
        CourseNotFoundException exception = new CourseNotFoundException(message);
        assertEquals(message, exception.getMessage());
    }
}