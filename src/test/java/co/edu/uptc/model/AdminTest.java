package co.edu.uptc.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para la entidad {@link Admin}.
 */
public class AdminTest {

    @Test
    @DisplayName("Debe construir un administrador heredando los atributos de User")
    public void testAdminInheritance() {
        Admin admin = new Admin("admin_root", "admin@uptc.edu.co", "rootpass", 99);

        assertEquals(99, admin.getId());
        assertEquals("admin_root", admin.getUserName());
        assertEquals("admin@uptc.edu.co", admin.getEmail());
        assertEquals("rootpass", admin.getPassword());
    }

    @Test
    @DisplayName("Debe instanciar un administrador usando el constructor por defecto")
    public void testDefaultConstructor() {
        Admin admin = new Admin();
        assertNotNull(admin);
    }
}