package co.edu.uptc.model;

import co.edu.uptc.interfaces.EducativeElementType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para la entidad {@link Lessons}.
 * 
 * @author @FELPBEC
 * @version v1.0
 */
public class LessonsTest {

    /**
     * Verifica la anteposición del prefijo "LESSON-" al configurar el ID.
     */
    @Test
    @DisplayName("Debe aplicar el prefijo LESSON- al ID de la lección")
    public void testLessonIdPrefix() {
        Lessons lesson = new Lessons();
        lesson.setId("20");
        assertEquals("LESSON-20", lesson.getId());
    }

    /**
     * Confirma que el tipo de elemento retornado sea LESSON.
     */
    @Test
    @DisplayName("Debe retornar el tipo de elemento LESSON")
    public void testGetElementType() {
        Lessons lesson = new Lessons();
        assertEquals(EducativeElementType.LESSON, lesson.getElementType());
    }

    /**
     * Valida la asignación y obtención de título, descripción y duración.
     */
    @Test
    @DisplayName("Debe asignar y recuperar los valores de las lecciones")
    public void testLessonFields() {
        Lessons lesson = new Lessons("LESSON-1", "Variables", "Tipos primitivos", 15.5);
        assertEquals("LESSON-1", lesson.getId());
        assertEquals("Variables", lesson.getTitle());
        assertEquals("Tipos primitivos", lesson.getDescription());
        assertEquals(15.5, lesson.getDuration());
    }
}