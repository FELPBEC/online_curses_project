package co.edu.uptc.interfaces;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para el contrato {@link EducativeElement}.
 * 
 * @author @FELPBEC
 * @version v1.0
 */
public class EducativeElementTest {

    /**
     * Implementación anónima/ficticia del contrato para validar su comportamiento.
     */
    private static class DummyEducativeElement implements EducativeElement {
        @Override
        public EducativeElementType getElementType() {
            return EducativeElementType.COURSE;
        }

        @Override
        public String getId() {
            return "ELEM-1";
        }

        @Override
        public String getTitle() {
            return "Título Genérico";
        }

        @Override
        public String getDescription() {
            return "Descripción Genérica";
        }
    }

    @Test
    @DisplayName("Debe cumplir con el contrato de métodos especificado por la interfaz")
    public void testEducativeElementContract() {
        EducativeElement element = new DummyEducativeElement();

        assertEquals(EducativeElementType.COURSE, element.getElementType());
        assertEquals("ELEM-1", element.getId());
        assertEquals("Título Genérico", element.getTitle());
        assertEquals("Descripción Genérica", element.getDescription());
    }
}