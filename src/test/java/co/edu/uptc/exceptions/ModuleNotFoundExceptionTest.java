package co.edu.uptc.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ModuleNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "El módulo solicitado no existe.";
        ModuleNotFoundException exception = assertThrows(
            ModuleNotFoundException.class,
            () -> { throw new ModuleNotFoundException(message); }
        );
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testIsRuntimeException() {
        ModuleNotFoundException exception = new ModuleNotFoundException("Error de prueba");
        assertTrue(exception instanceof RuntimeException);
    }
}