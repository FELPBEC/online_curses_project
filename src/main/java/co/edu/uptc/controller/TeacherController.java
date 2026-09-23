 package co.edu.uptc.controller;

import java.util.List;
import co.edu.uptc.exceptions.CredentialsAlreadyExistException;
import co.edu.uptc.exceptions.UserNotFoundException;
import co.edu.uptc.exceptions.InvalidFortmatException;
import co.edu.uptc.exceptions.WrongPasswordException;
import co.edu.uptc.interfaces.Repository;
import co.edu.uptc.model.Teacher;
import co.edu.uptc.util.PasswordSecurityService;
/**Clase controlador de profesores que maneja el CRUD de profesores
 * 
 * @author @FELPBEC
 * @version v1.0
 * @since 22/09/2026
 */
public class TeacherController {
    private final Repository<Teacher> repository;
    private List<Teacher> teacherList;
    private PasswordSecurityService security;

    /**Método constructor del controlador de profesores
     * 
     * @param repository repositorio para almacenar datos en persistencia
     */
    public TeacherController(Repository<Teacher> repository) {
        this.repository = repository;
        this.teacherList=repository.sendAll();
        this.security=new PasswordSecurityService();
    }
       //MÉTODOS AUXILIARES
     /**Método que envía la id del profesor atraves del nombre de usuario
     * 
     * @param userName nombre de usuario
     * @return id del profesor
     */
    private int sendIdByUserName(String userName){
        int id=0;
        for (int i = 0; i < teacherList.size(); i++) {
            if (userName.equals(teacherList.get(i).getUserName())) {
                id=teacherList.get(i).getId();
                break;
            }
        }
        return id;
    }
    
    /**Método que envía la id del profesor atraves del email
     * 
     * @param email correo electronico del profesor
     * @return id del profesor
     */
    private int sendIdByEmail(String email){
       int id=0;
        for (int i = 0; i < teacherList.size(); i++) {
            if (email.equals(teacherList.get(i).getEmail())) {
                id=teacherList.get(i).getId();
                break;
            }
        }
        return id;
    }
    /**Método que envía la nueva id autogenerada de un profesor
     * 
     * @return nueva id del nuevo profesor
     */
    private int sendNewId(){
        //Valor inicial para todas las IDs
        int biggestID=1;
        for (int i = 0; i < teacherList.size(); i++) {
            if (teacherList.get(i).getId()>biggestID) {
                biggestID=teacherList.get(i).getId();
            }
        }
        return biggestID;
    }
    //REGISTRAR

    /**Método para registrar un nuevo estudiante
     * 
     * Válida formato de contraseña correcta
     * Válida que el correo ni el nombre de usuario hallan sido registrados previamente
     * @param userName nombre de usuario
     * @param email correo electronico
     * @param password contraseña ingresada
     * 
     * @exception InvalidFortmatException excepción que surge si la contraseña no tiene el formato requerido
     * @exception CredentialsAlreadyExistException excepción que surge si las credenciales, ya sea correo o nombre de usuario ya existen
     */
    public void registerTeacher(String userName,String email, String password )throws InvalidFortmatException, CredentialsAlreadyExistException{
        if(!security.isValidFormat(password)) throw new InvalidFortmatException("El formato de la contraseña no es válido");
        if(sendIdByEmail(email)==0)throw new CredentialsAlreadyExistException("El correo ingresado ya existe");
        if(sendIdByUserName(userName)==0)throw new CredentialsAlreadyExistException("El nombre de usuario ingresado ya existe");
        int newId= sendNewId();
        String securityPassword=security.encrypt(password);
        teacherList.add(new Teacher(newId, userName, email, securityPassword));
        repository.saveAll(teacherList);
    }
    //INGRESAR
    /**Método que válida el login de un profesor
     * 
     * @param userOrEmail nombre de usuario o Correo Electronico
     * @param password contraseña
     * @return el login es exitoso retorna el profesor que se registro
     * @throws UserNotFoundException si el nombre de usuario o el correo no fueron encontrados
     * @throws WrongPasswordException si la contraseña es incorrecta
     */
    public Teacher joinTeacherAcount(String userOrEmail, String password) throws UserNotFoundException,WrongPasswordException{
        Teacher loginTeacher=null;
        //1. VERIFICACIÓN DE QUE EL USUARIO SI EXISTE YA SEA POR EMAIL O USUARIO
        if (sendIdByUserName(userOrEmail)!=0) {
            loginTeacher=sendTeacherById(sendIdByUserName(userOrEmail));
        } else if (sendIdByEmail(userOrEmail)!=0) {
            loginTeacher=sendTeacherById(sendIdByEmail(userOrEmail));
        }else{
            throw new UserNotFoundException("Nombre de usuario o correo electronico no encontrados");
        }

        //2. VERIFICACIÓN DE QUE LA CONTRASEÑA SEA LA CORRECTA
        if (!security.verify(password,loginTeacher.getPassword() )) {
            throw new WrongPasswordException("La contraseña ingresada no es correcta");
        }


        return loginTeacher;
        
    }


    //ELIMINAR ESTUDIANTE
    /**Método para eliminar un estudiante de la lista
     * 
     * @param id identificador único del estudiante
     */
    public void removeTeacher(int id)throws UserNotFoundException{
        if(!teacherWasFound(id))throw new UserNotFoundException("El estudiante de id:"+ id+ " no se encontró");
        teacherList.removeIf(e->e.getId()==id);
        repository.saveAll(teacherList);
    }
   
    
    //BUSCAR
    /**Método para buscar y enviar un estudiante por su Id
     * 
     * @param id identificador del estudiante
     * @return si lo encuentra retorna el objeto Estudiante, si no retorna un null
     */
    public Teacher sendTeacherById(int id){
        return teacherList.stream().filter(e->e.getId()==id).findFirst().orElse(null);
    }
    /**Método auxiliar que determina si un estudiante fue encontrado en la lista por su ID
     * 
     * @param id identificador del estudiante
     * @return booleano verdadero si lo encuentra y falso si no lo encuentra
     */
    public boolean teacherWasFound(int id){
        if (sendTeacherById(id)==null) {
            return false;
        }else{
            return true;
        }
    }
     //GUARDAR
    /**Método para guardar la lista de profesores en persistencia
     * 
     */
    public void saveAll(){
        repository.saveAll(teacherList);
    }

    //MÉTODOS PUNTUALES
    /**Método para asignar un curso a un profesor 
     * 
     * @param teacher profesor al que se le asginará el curso
     * @param idCourse id del curso asignado
     */
    public void addNewAssignedCourse(Teacher teacher, String idCourse){
        teacher.addNewCourse(idCourse);
    }
    /**Método para eliminar un curso de la lista de cursos asignados a un profesor
     * 
     * @param teacher profesor
     * @param idCourse id del curso a eliminar
     */
    public void removeAssignedCourse(Teacher teacher, String idCourse){
        teacher.removeAsignedCourse(idCourse);
    }
    
}