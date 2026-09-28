package co.edu.uptc.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvalidFortmatExceptionTest {

    @Test
    @DisplayName("Debe instanciar InvalidFortmatException con el mensaje correcto")
    public void testInvalidFortmatExceptionMessage() {
        String message = "Formato no válido";
        InvalidFortmatException exception = new InvalidFortmatException(message);
        assertEquals(message, exception.getMessage());
    }
}