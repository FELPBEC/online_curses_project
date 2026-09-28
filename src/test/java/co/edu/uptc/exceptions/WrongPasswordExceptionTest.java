package co.edu.uptc.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WrongPasswordExceptionTest {

    @Test
    @DisplayName("Debe instanciar WrongPasswordException con el mensaje correcto")
    public void testWrongPasswordExceptionMessage() {
        String message = "Contraseña incorrecta";
        WrongPasswordException exception = new WrongPasswordException(message);
        assertEquals(message, exception.getMessage());
    }
}