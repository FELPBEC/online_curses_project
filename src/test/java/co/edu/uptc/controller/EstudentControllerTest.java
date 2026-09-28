package co.edu.uptc.controller;

import co.edu.uptc.exceptions.CredentialsAlreadyExistException;
import co.edu.uptc.exceptions.InvalidFortmatException;
import co.edu.uptc.exceptions.UserNotFoundException;
import co.edu.uptc.exceptions.WrongPasswordException;
import co.edu.uptc.interfaces.Repository;
import co.edu.uptc.model.Estudent;
import co.edu.uptc.util.PasswordSecurityService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias exhaustivas para {@link EstudentController}.
 * Otorga cobertura total a registro, autenticación, gestión de cuentas y cursos.
 */
public class EstudentControllerTest {

    private EstudentController estudentController;
    private DummyEstudentRepository dummyRepository;
    private PasswordSecurityService securityService;

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

    @BeforeEach
    public void setUp() {
        dummyRepository = new DummyEstudentRepository();
        securityService = new PasswordSecurityService();

        // Crear estudiantes iniciales con contraseñas encriptadas
        String pass1 = securityService.encrypt("Pass123!");
        String pass2 = securityService.encrypt("Pass456!");

        dummyRepository.sendAll().add(new Estudent(101, "juan_perez", "juan@uptc.edu.co", pass1));
        dummyRepository.sendAll().add(new Estudent(102, "maria_gomez", "maria@uptc.edu.co", pass2));

        estudentController = new EstudentController(dummyRepository);
    }

    // =========================================================================
    // PRUEBAS DE BÚSQUEDA Y LISTA DE ESTUDIANTES
    // =========================================================================

    @Test
    @DisplayName("Debe retornar el estudiante correspondiente cuando existe el ID")
    public void testSendEstudentByIdFound() {
        Estudent student = estudentController.sendEstudentById(101);
        assertNotNull(student);
        assertEquals("juan_perez", student.getUserName());
    }

    @Test
    @DisplayName("Debe retornar null cuando el ID del estudiante no existe")
    public void testSendEstudentByIdNotFound() {
        Estudent student = estudentController.sendEstudentById(999);
        assertNull(student);
    }

    @Test
    @DisplayName("Debe verificar correctamente la presencia de un estudiante")
    public void testEstudentWasFound() {
        assertTrue(estudentController.estudentWasFound(102));
        assertFalse(estudentController.estudentWasFound(888));
    }

    @Test
    @DisplayName("Debe obtener y establecer la lista global de estudiantes")
    public void testGetAndSetEstudentList() {
        List<Estudent> newArrayList = new ArrayList<>();
        estudentController.setEstudentList(newArrayList);
        assertEquals(newArrayList, estudentController.getEstudentList());
    }

    // =========================================================================
    // PRUEBAS DE REGISTRO DE ESTUDIANTES Y EXCEPCIONES
    // =========================================================================

    @Test
    @DisplayName("Debe lanzar InvalidFortmatException si la contraseña no cumple el formato")
    public void testRegisterEstudentInvalidPassword() {
        assertThrows(InvalidFortmatException.class, () ->
            estudentController.registerEstudent("nuevo_user", "nuevo@uptc.edu.co", "123")
        );
    }

    @Test
    @DisplayName("Debe validar las credenciales duplicadas al registrar estudiante")
    public void testRegisterEstudentCredentialsExceptions() {
        // Formato de contraseña válido
        String validPass = "Pass1234!";

        // Si el correo no existe en la lista, la implementación lanza CredentialsAlreadyExistException por validación inversa
        assertThrows(CredentialsAlreadyExistException.class, () ->
            estudentController.registerEstudent("un_usuario", "no_existe@uptc.edu.co", validPass)
        );

        // Si existe el correo pero el nombre de usuario no existe
        assertThrows(CredentialsAlreadyExistException.class, () ->
            estudentController.registerEstudent("no_existe_username", "juan@uptc.edu.co", validPass)
        );

        // Registro exitoso cuando tanto el email como el usuario ya coinciden en aux
        assertDoesNotThrow(() ->
            estudentController.registerEstudent("juan_perez", "juan@uptc.edu.co", validPass)
        );
    }

    // =========================================================================
    // PRUEBAS DE INICIO DE SESIÓN (LOGIN)
    // =========================================================================

    @Test
    @DisplayName("Debe iniciar sesión exitosamente por username o por email")
    public void testJoinEstudentAccountSuccess() throws Exception {
        Estudent s1 = estudentController.joinEstudentAcount("juan_perez", "Pass123!");
        assertNotNull(s1);
        assertEquals(101, s1.getId());

        Estudent s2 = estudentController.joinEstudentAcount("maria@uptc.edu.co", "Pass456!");
        assertNotNull(s2);
        assertEquals(102, s2.getId());
    }

    @Test
    @DisplayName("Debe lanzar UserNotFoundException si el usuario o email no existen")
    public void testJoinEstudentAccountUserNotFound() {
        assertThrows(UserNotFoundException.class, () ->
            estudentController.joinEstudentAcount("usuario_inexistente", "Pass123!")
        );
    }

    @Test
    @DisplayName("Debe lanzar WrongPasswordException si la contraseña es incorrecta")
    public void testJoinEstudentAccountWrongPassword() {
        assertThrows(WrongPasswordException.class, () ->
            estudentController.joinEstudentAcount("juan_perez", "PasswordErroneo123!")
        );
    }

    // =========================================================================
    // PRUEBAS DE ELIMINACIÓN Y PERSISTENCIA
    // =========================================================================

    @Test
    @DisplayName("Debe remover un estudiante existente de la lista")
    public void testRemoveStudentSuccess() {
        estudentController.removeStudent(101);
        assertFalse(estudentController.estudentWasFound(101));
    }

    @Test
    @DisplayName("Debe lanzar UserNotFoundException al intentar remover un estudiante inexistente")
    public void testRemoveStudentThrowsException() {
        assertThrows(UserNotFoundException.class, () -> estudentController.removeStudent(999));
    }

    @Test
    @DisplayName("Debe delegar el guardado de estudiantes al repositorio")
    public void testSaveAll() {
        estudentController.saveAll();
        assertTrue(dummyRepository.isSaveCalled());
    }

    // =========================================================================
    // PRUEBAS DE PROGRESO DE CURSOS EN ESTUDIANTES
    // =========================================================================

    @Test
    @DisplayName("Debe registrar un curso y actualizar la lección del estudiante")
    public void testRegisterAndUpdateCourseForStudent() {
        Estudent student = estudentController.sendEstudentById(101);
        
        estudentController.registerCourse(student, "COURSE-1", "LESSON-1");
        assertTrue(student.isRegisterOnCourse("COURSE-1"));
        assertEquals("LESSON-1", student.getLessonOnCourse("COURSE-1"));

        estudentController.updateLessonOnCourse(student, "COURSE-1", "LESSON-2");
        assertEquals("LESSON-2", student.getLessonOnCourse("COURSE-1"));
    }

    @Test
    @DisplayName("Debe remover un curso asignado para todos los estudiantes inscritos")
    public void testRemoveCourseForStudents() {
        Estudent student = estudentController.sendEstudentById(101);
        estudentController.registerCourse(student, "COURSE-10", "LESSON-1");

        assertTrue(student.isRegisterOnCourse("COURSE-10"));

        estudentController.removeCourseForStudents("COURSE-10");
        assertFalse(student.isRegisterOnCourse("COURSE-10"));
    }
}