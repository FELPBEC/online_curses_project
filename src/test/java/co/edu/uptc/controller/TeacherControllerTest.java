package co.edu.uptc.controller;

import co.edu.uptc.exceptions.CredentialsAlreadyExistException;
import co.edu.uptc.exceptions.InvalidFortmatException;
import co.edu.uptc.exceptions.UserNotFoundException;
import co.edu.uptc.exceptions.WrongPasswordException;
import co.edu.uptc.interfaces.Repository;
import co.edu.uptc.model.Teacher;
import co.edu.uptc.util.PasswordSecurityService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TeacherControllerTest {

    private TeacherController teacherController;
    private DummyTeacherRepository dummyRepository;
    private PasswordSecurityService securityService;

    private static class DummyTeacherRepository implements Repository<Teacher> {
        private List<Teacher> teachers = new ArrayList<>();
        private boolean saveCalled = false;

        @Override
        public List<Teacher> sendAll() {
            return teachers;
        }

        @Override
        public void saveAll(List<Teacher> list) {
            this.saveCalled = true;
            this.teachers = new ArrayList<>(list);
        }

        public boolean isSaveCalled() {
            return saveCalled;
        }
    }

    @BeforeEach
    public void setUp() {
        dummyRepository = new DummyTeacherRepository();
        teacherController = new TeacherController(dummyRepository);
        securityService = new PasswordSecurityService();
    }

    @Test
    @DisplayName("Debe obtener y establecer la lista de profesores")
    public void testGetAndSetTeacherList() {
        List<Teacher> newList = new ArrayList<>();
        newList.add(new Teacher(1, "profesor1", "profe1@test.com", "pass1"));

        teacherController.setTeacherList(newList);
        assertEquals(1, teacherController.getTeacherList().size());
        assertEquals("profesor1", teacherController.getTeacherList().get(0).getUserName());
    }

    @Test
    @DisplayName("Debe lanzar InvalidFortmatException si la contraseña no cumple formato al registrar profesor")
    public void testRegisterTeacherInvalidPasswordFormat() {
        assertThrows(InvalidFortmatException.class, () -> 
            teacherController.registerTeacher("profeUser", "profe@test.com", "123")
        );
    }

    @Test
    @DisplayName("Debe validar credenciales existentes al registrar profesor")
    public void testRegisterTeacherCredentialsValidation() {
        String validPass = "SecurePass123!";
        assertThrows(CredentialsAlreadyExistException.class, () -> 
            teacherController.registerTeacher("profeUser", "profe@test.com", validPass)
        );
    }

    @Test
    @DisplayName("Debe registrar profesor correctamente cuando hay un profesor existente")
    public void testRegisterTeacherSuccess() {
        String validPass = "SecurePass123!";
        Teacher existingTeacher = new Teacher(1, "profeExist", "profe@test.com", "hashedPass");
        dummyRepository.sendAll().add(existingTeacher);

        assertDoesNotThrow(() -> 
            teacherController.registerTeacher("profeExist", "profe@test.com", validPass)
        );
        assertTrue(dummyRepository.isSaveCalled());
    }

    @Test
    @DisplayName("Debe iniciar sesión correctamente por usuario o email")
    public void testJoinTeacherAccountSuccess() {
        // Encriptar contraseña e insertar directamente el profesor en el repositorio de prueba
        String encryptedPass = securityService.encrypt("SecurePass123!");
        Teacher teacher = new Teacher(1, "profe1", "profe1@test.com", encryptedPass);
        dummyRepository.sendAll().add(teacher);

        // Iniciar sesión por usuario
        assertDoesNotThrow(() -> {
            Teacher loggedTeacher = teacherController.joinTeacherAcount("profe1", "SecurePass123!");
            assertNotNull(loggedTeacher);
        });

        // Iniciar sesión por email
        assertDoesNotThrow(() -> {
            Teacher loggedTeacher = teacherController.joinTeacherAcount("profe1@test.com", "SecurePass123!");
            assertNotNull(loggedTeacher);
        });
    }

    @Test
    @DisplayName("Debe lanzar UserNotFoundException si el profesor no existe")
    public void testJoinTeacherAccountNotFound() {
        assertThrows(UserNotFoundException.class, () -> 
            teacherController.joinTeacherAcount("inexistente", "SecurePass123!")
        );
    }

    @Test
    @DisplayName("Debe lanzar WrongPasswordException si la contraseña de profesor es incorrecta")
    public void testJoinTeacherAccountWrongPassword() {
        // Encriptar contraseña e insertar directamente el profesor en el repositorio de prueba
        String encryptedPass = securityService.encrypt("SecurePass123!");
        Teacher teacher = new Teacher(1, "profe1", "profe1@test.com", encryptedPass);
        dummyRepository.sendAll().add(teacher);

        assertThrows(WrongPasswordException.class, () -> 
            teacherController.joinTeacherAcount("profe1", "WrongPass123!")
        );
    }

    @Test
    @DisplayName("Debe eliminar un profesor existente y lanzar excepción si no existe")
    public void testRemoveTeacher() {
        Teacher teacher = new Teacher(3, "profe3", "profe3@test.com", "pass");
        dummyRepository.sendAll().add(teacher);

        assertTrue(teacherController.teacherWasFound(3));

        // Eliminar existente
        assertDoesNotThrow(() -> teacherController.removeTeacher(3));
        assertFalse(teacherController.teacherWasFound(3));

        // Intentar eliminar inexistente
        assertThrows(UserNotFoundException.class, () -> teacherController.removeTeacher(999));
    }

    @Test
    @DisplayName("Debe buscar profesor por ID y retornar null si no existe")
    public void testSendTeacherById() {
        Teacher teacher = new Teacher(10, "profe10", "profe10@test.com", "pass");
        dummyRepository.sendAll().add(teacher);

        assertEquals(teacher, teacherController.sendTeacherById(10));
        assertNull(teacherController.sendTeacherById(99));
    }

    @Test
    @DisplayName("Debe invocar el guardado de la lista en persistencia")
    public void testSaveAll() {
        teacherController.saveAll();
        assertTrue(dummyRepository.isSaveCalled());
    }

    @Test
    @DisplayName("Debe asignar y remover cursos a un profesor")
    public void testAddAndRemoveAssignedCourse() {
        Teacher teacher = new Teacher(1, "profe1", "p1@test.com", "pass");

        teacherController.addNewAssignedCourse(teacher, "COURSE-100");
        assertTrue(teacher.getAsginedCourses().contains("COURSE-100"));

        teacherController.removeAssignedCourse(teacher, "COURSE-100");
        assertFalse(teacher.getAsginedCourses().contains("COURSE-100"));
    }
}