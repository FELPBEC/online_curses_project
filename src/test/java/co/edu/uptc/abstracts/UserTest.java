package co.edu.uptc.abstracts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para la clase abstracta {@link User}.
 * Emplea una implementación concreta simulada para validar la lógica base.
 * 
 * @author @FELPBEC
 * @version v1.0
 */
public class UserTest {

    private TestUser user;

    /**
     * Subclase concreta para probar la clase abstracta User.
     */
    private static class TestUser extends User {
        public TestUser() {
            super();
        }

        public TestUser(String userName, String email, String password, int id) {
            super(userName, email, password, id);
        }
    }

    @BeforeEach
    public void setUp() {
        user = new TestUser("usuario_test", "test@uptc.edu.co", "password123", 1);
    }

    @Test
    @DisplayName("Debe instanciar y retornar los atributos configurados correctamente")
    public void testUserGettersAndSetters() {
        assertEquals(1, user.getId());
        assertEquals("usuario_test", user.getUserName());
        assertEquals("test@uptc.edu.co", user.getEmail());
        assertEquals("password123", user.getPassword());

        user.setId(2);
        user.setUserName("nuevo_usuario");
        user.setEmail("nuevo@uptc.edu.co");
        user.setPassword("nueva_clave");

        assertEquals(2, user.getId());
        assertEquals("nuevo_usuario", user.getUserName());
        assertEquals("nuevo@uptc.edu.co", user.getEmail());
        assertEquals("nueva_clave", user.getPassword());
    }

    @Test
    @DisplayName("Debe validar credenciales del usuario correctamente")
    public void testValidateCredentials() {
        String emailRef = "test@uptc.edu.co";
        String passwordRef = "password123";
        user.setEmail(emailRef);
        user.setPassword(passwordRef);

        assertTrue(user.validateCredentials(emailRef, passwordRef));
        assertFalse(user.validateCredentials("otro@uptc.edu.co", passwordRef));
    }
}