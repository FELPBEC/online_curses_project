package co.edu.uptc.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para la entidad {@link Teacher}.
 */
public class TeacherTest {

    @Test
    @DisplayName("Debe construir un administrador heredando los atributos de User")
    public void testTeacherInheritance() {
        Teacher teacher= new Teacher( 99, "admin_root", "admin@uptc.edu.co", "rootpass");

        assertEquals(99, teacher.getId());
        assertEquals("admin_root", teacher.getUserName());
        assertEquals("admin@uptc.edu.co", teacher.getEmail());
        assertEquals("rootpass", teacher.getPassword());
    }

    @Test
    @DisplayName("Debe instanciar un administrador usando el constructor por defecto")
    public void testDefaultConstructor() {
        Teacher admin = new Teacher();
        assertNotNull(admin);
    }
}