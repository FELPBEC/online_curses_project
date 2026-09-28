package co.edu.uptc.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para {@link EstudentNotFoundException}.
 * 
 * @author @FELPBEC
 * @version v1.0
 */
public class UserNotFoundExceptionTest {

    @Test
    @DisplayName("Debe instanciar la excepción con el mensaje de error")
    public void testUserNotFoundExceptionMessage() {
        UserNotFoundException exception = new UserNotFoundException("Estudiante no encontrado");

        assertEquals("Estudiante no encontrado", exception.getMessage());
    }
}