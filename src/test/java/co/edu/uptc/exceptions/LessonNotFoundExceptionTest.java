package co.edu.uptc.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LessonNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "La lección solicitada no existe.";
        LessonNotFoundException exception = assertThrows(
            LessonNotFoundException.class,
            () -> { throw new LessonNotFoundException(message); }
        );
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testIsRuntimeException() {
        LessonNotFoundException exception = new LessonNotFoundException("Error de prueba");
        assertTrue(exception instanceof RuntimeException);
    }
}
