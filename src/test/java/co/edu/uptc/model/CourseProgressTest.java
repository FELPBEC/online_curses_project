package co.edu.uptc.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CourseProgressTest {

    @Test
    @DisplayName("Debe probar los constructores y métodos getters y setters de CourseProgress")
    public void testCourseProgressGettersAndSetters() {
        // Constructor vacío
        CourseProgress progressEmpty = new CourseProgress();
        assertNull(progressEmpty.getIdLesson());
        assertFalse(progressEmpty.isCompleteState());

        // Constructor con parámetro
        CourseProgress progressParam = new CourseProgress("LESSON-1");
        assertEquals("LESSON-1", progressParam.getIdLesson());
        assertFalse(progressParam.isCompleteState());

        // Probar Setters
        progressParam.setIdLesson("LESSON-5");
        assertEquals("LESSON-5", progressParam.getIdLesson());

        progressParam.setCompleteState(true);
        assertTrue(progressParam.isCompleteState());
    }
}