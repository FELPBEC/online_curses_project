package co.edu.uptc.model;

import co.edu.uptc.interfaces.EducativeElementType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para la entidad {@link Course}.
 * 
 * @author @FELPBEC
 * @version v1.0
 */
public class CourseTest {

    /**
     * Prueba la instanciación y la adición del prefijo "COURSE-" en el identificador.
     */
    @Test
    @DisplayName("Debe formatear el ID adecuadamente con el prefijo COURSE-")
    public void testCourseIdFormatting() {
        Course course = new Course();
        course.setId("1");
        assertEquals("COURSE-1", course.getId());
    }

    /**
     * Prueba que el método getElementType retorne EducativeElementType.COURSE.
     */
    @Test
    @DisplayName("Debe retornar el tipo de elemento educativo COURSE")
    public void testGetElementType() {
        Course course = new Course();
        assertEquals(EducativeElementType.COURSE, course.getElementType());
    }

    /**
     * Prueba la asignación de atributos y asignación de nodo raíz.
     */
    @Test
    @DisplayName("Debe asignar y obtener las propiedades correctamente")
    public void testCourseGettersAndSetters() {
        TreeNode<co.edu.uptc.interfaces.EducativeElement> root = new TreeNode<>();
        Course course = new Course("1", "Estructuras", root);
        course.setDescription("Descripción general");

        assertEquals("COURSE-1", course.getId());
        assertEquals("Estructuras", course.getTitle());
        assertEquals("Descripción general", course.getDescription());
        assertEquals(root, course.getRoot());
    }
}