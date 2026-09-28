package co.edu.uptc.controller;

import co.edu.uptc.exceptions.EstudentNotFoundException;
import co.edu.uptc.interfaces.Repository;
import co.edu.uptc.model.Estudent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para {@link EstudentController}.
 * Valida la búsqueda, verificación, eliminación y persistencia de estudiantes.
 * 
 * @author @FELPBEC
 * @version v1.0
 */
public class EstudentControllerTest {

    private EstudentController estudentController;
    private DummyEstudentRepository dummyRepository;

    /**
     * Repositorio simulado en memoria para las pruebas de estudiantes.
     */
    private static class DummyEstudentRepository implements Repository<Estudent> {
        private final List<Estudent> students = new ArrayList<>();
        private boolean saveCalled = false;

        @Override
        public List<Estudent> sendAll() {
            return students;
        }

        @Override
        public void saveAll(List<Estudent> list) {
            this.saveCalled = true;
        }

        public boolean isSaveCalled() {
            return saveCalled;
        }
    }

    /**
     * Inicialización del controlador con datos iniciales de prueba.
     */
    @BeforeEach
    public void setUp() {
        dummyRepository = new DummyEstudentRepository();
        dummyRepository.sendAll().add(new Estudent(101, "juan_perez", "juan@uptc.edu.co", "pass123"));
        dummyRepository.sendAll().add(new Estudent(102, "maria_gomez", "maria@uptc.edu.co", "pass456"));

        estudentController = new EstudentController(dummyRepository);
    }

    /**
     * Prueba la búsqueda de un estudiante por su ID.
     */
    @Test
    @DisplayName("Debe retornar el estudiante correspondiente cuando existe el ID")
    public void testSendEstudentByIdFound() {
        Estudent student = estudentController.sendEstudentById(101);
        assertNotNull(student);
        assertEquals("juan_perez", student.getUserName());
    }

    /**
     * Prueba la búsqueda de un estudiante inexistente.
     */
    @Test
    @DisplayName("Debe retornar null cuando el ID del estudiante no existe")
    public void testSendEstudentByIdNotFound() {
        Estudent student = estudentController.sendEstudentById(999);
        assertNull(student);
    }

    /**
     * Prueba el método de verificación de existencia por ID.
     */
    @Test
    @DisplayName("Debe verificar correctamente la presencia de un estudiante")
    public void testEstudentWasFound() {
        assertTrue(estudentController.estudentWasFound(102));
        assertFalse(estudentController.estudentWasFound(888));
    }

    /**
     * Prueba la eliminación exitosa de un estudiante.
     */
    @Test
    @DisplayName("Debe remover un estudiante existente de la lista")
    public void testRemoveStudentSuccess() {
        estudentController.removeStudent(101);
        assertFalse(estudentController.estudentWasFound(101));
    }

    /**
     * Prueba que la eliminación de un estudiante inexistente lance {@link EstudentNotFoundException}.
     */
    @Test
    @DisplayName("Debe lanzar EstudentNotFoundException al intentar remover un estudiante inexistente")
    public void testRemoveStudentThrowsException() {
        assertThrows(EstudentNotFoundException.class, () -> estudentController.removeStudent(999));
    }

    /**
     * Prueba la invocación de persistencia para guardar la lista de estudiantes.
     */
    @Test
    @DisplayName("Debe delegar el guardado de estudiantes al repositorio")
    public void testSaveAll() {
        estudentController.saveAll();
        assertTrue(dummyRepository.isSaveCalled());
    }
}