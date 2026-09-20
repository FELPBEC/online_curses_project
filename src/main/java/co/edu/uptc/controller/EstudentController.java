package co.edu.uptc.controller;

import java.util.List;

import co.edu.uptc.exceptions.EstudentNotFoundException;
import co.edu.uptc.interfaces.Repository;
import co.edu.uptc.model.Estudent;
/**Clase EstudentController que maneja el CRUD de los estudiantes
 * 
 * @author @FELPBEC
 * @version v1.0
 * @since 19/09/2026
 */
public class EstudentController {
    private final Repository<Estudent> repository;
    private List<Estudent> estudentList;
    /**Constructor de la clase EstudentController 
     * 
     * @param repository repositorio generico de cualquier tipo
     * 
     * NOTA: la lista se instancia desde el método "sendAll()" del repositorio al crear un controller
     */
    public EstudentController(Repository<Estudent> repository) {
        this.repository = repository;
        this.estudentList=repository.sendAll();
    }
    /**Método para guardar la lista de Estudiantes en persistencia
     * 
     */
    public void saveAll(){
        repository.saveAll(estudentList);
    }
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
    /**Método para eliminar un estudiante de la lista
     * 
     * @param id identificador único del estudiante
     */
    public void removeStudent(int id){
        if(!estudentWasFound(id))throw new EstudentNotFoundException("El estudiante de id:"+ id+ " no se encontró");
        estudentList.removeIf(e->e.getId()==id);
    }
    

}
