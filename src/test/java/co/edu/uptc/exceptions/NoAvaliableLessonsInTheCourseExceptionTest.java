package co.edu.uptc.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NoAvaliableLessonsInTheCourseExceptionTest {

    @Test
    @DisplayName("Debe instanciar NoAvaliableLessonsInTheCourseException con el mensaje correcto")
    public void testNoAvaliableLessonsInTheCourseExceptionMessage() {
        String message = "No hay lecciones disponibles";
        NoAvaliableLessonsInTheCourseException exception = new NoAvaliableLessonsInTheCourseException(message);
        assertEquals(message, exception.getMessage());
    }
}