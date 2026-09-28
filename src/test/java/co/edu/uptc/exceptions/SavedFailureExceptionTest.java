package co.edu.uptc.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la excepción personalizada {@link SavedFailureException}.
 */
public class SavedFailureExceptionTest {

    @Test
    @DisplayName("Debe construir la excepción preservando el mensaje y la causa original")
    public void testExceptionAttributes() {
        Throwable cause = new RuntimeException("Causa raíz de E/S");
        SavedFailureException exception = new SavedFailureException("Error al guardar archivo", cause);

        assertEquals("Error al guardar archivo", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}