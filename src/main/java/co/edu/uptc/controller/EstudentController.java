package co.edu.uptc.controller;

import java.util.List;

import co.edu.uptc.exceptions.CredentialsAlreadyExistException;
import co.edu.uptc.exceptions.UserNotFoundException;
import co.edu.uptc.exceptions.InvalidFortmatException;
import co.edu.uptc.exceptions.WrongPasswordException;
import co.edu.uptc.interfaces.Repository;
import co.edu.uptc.model.Estudent;
import co.edu.uptc.util.PasswordSecurityService;
/**Clase EstudentController que maneja el CRUD de los estudiantes
 * 
 * @author @FELPBEC
 * @version v1.0
 * @since 19/09/2026
 */
public class EstudentController {
    private final Repository<Estudent> repository;
    private List<Estudent> estudentList;
    private PasswordSecurityService security;
    /**Constructor de la clase EstudentController 
     * 
     * @param repository repositorio generico de cualquier tipo
     * 
     * NOTA: la lista se instancia desde el método "sendAll()" del repositorio al crear un controller
     */
    public EstudentController(Repository<Estudent> repository) {
        this.repository = repository;
        this.estudentList=repository.sendAll();
        this.security= new PasswordSecurityService();
    }
   
    //MÉTODOS AUXILIARES
     /**Método que envía la id del estudiante atraves del nombre de usuario
     * 
     * @param userName nombre de usuario
     * @return id del estudiante
     */
    private int sendIdByUserName(String userName){
        int id=0;
        for (int i = 0; i < estudentList.size(); i++) {
            if (userName.equals(estudentList.get(i).getUserName())) {
                id=estudentList.get(i).getId();
                break;
            }
        }
        return id;
    }
    
    /**Método que envía la id del estudiante atraves del email
     * 
     * @param email correo electronico del estudiante
     * @return id del estudiante
     */
    private int sendIdByEmail(String email){
       int id=0;
        for (int i = 0; i < estudentList.size(); i++) {
            if (email.equals(estudentList.get(i).getEmail())) {
                id=estudentList.get(i).getId();
                break;
            }
        }
        return id;
    }
    /**Método que envía la nueva id autogenerada de un estudiante
     * 
     * @return nueva id del nuevo estudiante
     */
    private int sendNewId(){
        //Valor inicial para todas las IDs
        int biggestID=1;
        for (int i = 0; i < estudentList.size(); i++) {
            if (estudentList.get(i).getId()>biggestID) {
                biggestID=estudentList.get(i).getId();
            }
        }
        return biggestID;
    }
    //REGISTRAR

    /**Método para registrar un nuevo estudiante
     * 
     * Válida formato de contraseña correcta
     * Válida que el correo ni el nombre de usuario hallan sido usados previamente
     * @param userName nombre de usuario
     * @param email correo electronico
     * @param password contraseña ingresada
     * 
     * @exception InvalidFortmatException excepción que surge si la contraseña no tiene el formato requerido
     * @exception CredentialsAlreadyExistException excepción que surge si las credenciales, ya sea correo o nombre de usuario ya existen
     */
    public void registerEstudent(String userName,String email, String password )throws InvalidFortmatException, CredentialsAlreadyExistException{
        if(!security.isValidFormat(password)) throw new InvalidFortmatException("El formato de la contraseña no es válido");
        if(sendIdByEmail(email)==0)throw new CredentialsAlreadyExistException("El correo ingresado ya existe");
        if(sendIdByUserName(userName)==0)throw new CredentialsAlreadyExistException("El nombre de usuario ingresado ya existe");
        int newId= sendNewId();
        String securityPassword=security.encrypt(password);
        estudentList.add(new Estudent(newId, userName, email, securityPassword));
        repository.saveAll(estudentList);
    }
    //INGRESAR
    public Estudent joinEstudentAcount(String userOrEmail, String password) throws UserNotFoundException,WrongPasswordException{
        Estudent loginEstudent=null;
        //1. VERIFICACIÓN DE QUE EL USUARIO SI EXISTE YA SEA POR EMAIL O USUARIO
        if (sendIdByUserName(userOrEmail)!=0) {
            loginEstudent=sendEstudentById(sendIdByUserName(userOrEmail));
        } else if (sendIdByEmail(userOrEmail)!=0) {
            loginEstudent=sendEstudentById(sendIdByEmail(userOrEmail));
        }else{
            throw new UserNotFoundException("Nombre de usuario o correo electronico no encontrados");
        }

        //2. VERIFICACIÓN DE QUE LA CONTRASEÑA SEA LA CORRECTA
        if (!security.verify(password,loginEstudent.getPassword() )) {
            throw new WrongPasswordException("La contraseña ingresada no es correcta");
        }


        return loginEstudent;
        
    }


    //ELIMINAR ESTUDIANTE
    /**Método para eliminar un estudiante de la lista
     * 
     * @param id identificador único del estudiante
     */
    public void removeStudent(int id)throws UserNotFoundException{
        if(!estudentWasFound(id))throw new UserNotFoundException("El estudiante de id:"+ id+ " no se encontró");
        estudentList.removeIf(e->e.getId()==id);
        repository.saveAll(estudentList);
    }
   
    
    //BUSCAR
    /**Método para buscar y enviar un estudiante por su Id
     * 
     * @param id identificador del estudiante
     * @return si lo encuentra retorna el objeto Estudiante, si no retorna un null
     */
    public Estudent sendEstudentById(int id){
        return estudentList.stream().filter(e->e.getId()==id).findFirst().orElse(null);
    }
    /**Método auxiliar que determina si un estudiante fue encontrado en la lista por su ID
     * 
     * @param id identificador del estudiante
     * @return booleano verdadero si lo encuentra y falso si no lo encuentra
     */
    public boolean estudentWasFound(int id){
        if (sendEstudentById(id)==null) {
            return false;
        }else{
            return true;
        }
    }
     //GUARDAR
    /**Método para guardar la lista de Estudiantes en persistencia
     * 
     */
    public void saveAll(){
        repository.saveAll(estudentList);
    }

    //MÉTODOS PUNTUALES
    /**Método para que un Estudiante se registre en un curso
     * 
     * @param estudent Estudiante que se registrara en un curso
     * @param idCourse  id del curso
     * @param idFirstLesson id de la primera lección
     */
    public void registerCourse(Estudent estudent, String idCourse, String idFirstLesson){
        estudent.registerCourse(idCourse, idFirstLesson);
    }
    /**Método para actualizar la lección de un curso en que un estudiante esta registrado
     * 
     * @param estudent estudiante al que se le actualizará la lección
     * @param idCourse identificador del curso
     * @param idNextLesson identificador de la lección actualizada
     */
    public void updateLessonOnCourse(Estudent estudent, String idCourse, String idNextLesson){
        estudent.goToNextLesson(idCourse, idNextLesson);
    }
    

}
