package co.edu.uptc.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para la entidad {@link Estudent}.
 */
public class EstudentTest {

    @Test
    @DisplayName("Debe registrar la inscripción a un curso y retornar la lección actual")
    public void testRegisterCourseAndGetLesson() {
        Estudent student = new Estudent(1, "estudiante1", "estudiante1@uptc.edu.co", "secret123");
        student.registerCourse("COURSE-1", "LESSON-1");

        assertEquals("LESSON-1", student.getLessonOnCourse("COURSE-1"), 
                "Debe retornar la primera lección asignada al curso");
    }

    @Test
    @DisplayName("Debe gestionar el mapa de progreso mediante getCoursesProgress y setCoursesProgress")
    public void testCoursesProgressGettersAndSetters() {
        Estudent student = new Estudent();
        assertNull(student.getCoursesProgress());

        Map<String, String> progressMap = new HashMap<>();
        progressMap.put("COURSE-100", "LESSON-50");
        student.setCoursesProgress(progressMap);

        assertNotNull(student.getCoursesProgress());
        assertEquals(1, student.getCoursesProgress().size());
        assertEquals("LESSON-50", student.getLessonOnCourse("COURSE-100"));
    }
}