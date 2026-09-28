package co.edu.uptc.model;

import co.edu.uptc.interfaces.EducativeElementType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para la entidad {@link Module}.
 * 
 * @author @FELPBEC
 * @version v1.0
 */
public class ModuleTest {

    /**
     * Prueba el formateo automático del ID con el prefijo "MODULE-".
     */
    @Test
    @DisplayName("Debe anteponer el prefijo MODULE- al asignar la ID")
    public void testModuleIdPrefix() {
        Module module = new Module();
        module.setId("5");
        assertEquals("MODULE-5", module.getId());
    }

    /**
     * Verifica que el tipo de elemento corresponda a MODULO.
     */
    @Test
    @DisplayName("Debe retornar el tipo de elemento educativo MODULO")
    public void testGetElementType() {
        Module module = new Module();
        assertEquals(EducativeElementType.MODULO, module.getElementType());
    }

    /**
     * Prueba los constructores y métodos de acceso.
     */
    @Test
    @DisplayName("Debe inicializar las propiedades mediante el constructor parametrizado")
    public void testConstructorAndGetters() {
        Module module = new Module("MODULE-1", "Módulo Basico", "Introducción");
        assertEquals("MODULE-1", module.getId());
        assertEquals("Módulo Basico", module.getTitle());
        assertEquals("Introducción", module.getDescription());
    }
}