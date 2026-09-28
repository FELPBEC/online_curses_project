package co.edu.uptc.interfaces;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para el enumerado {@link EducativeElementType}.
 * 
 * @author @FELPBEC
 * @version v1.0
 */
public class EducativeElementTypeTest {

    /**
     * Valida que existan los tres valores enum definidos para los elementos educativos.
     */
    @Test
    @DisplayName("Debe contener exactamente los tres tipos de elementos educativos permitidos")
    public void testEnumValues() {
        EducativeElementType[] values = EducativeElementType.values();
        assertEquals(3, values.length);
        assertEquals(EducativeElementType.COURSE, EducativeElementType.valueOf("COURSE"));
        assertEquals(EducativeElementType.MODULO, EducativeElementType.valueOf("MODULO"));
        assertEquals(EducativeElementType.LESSON, EducativeElementType.valueOf("LESSON"));
    }
}