package co.edu.uptc.model;
/**Clase CourseProgress  que almacena el progreso de un estudiante en un curso
 * por medio de la id de la lección actual y un estado de completado o incompletado
 * 
 * @author @FELPBEC
 * @version v1.0
 * @since 26/09/2026
 */
public class CourseProgress {
    private String idLesson;
    private boolean completeState;

    /**Contructor vacío para carga de la clase desde repositorio remoto
     * 
     */
    public CourseProgress() {
    }
    /**Método constructor con párametros que recibe la id de la priemera lección del curso
     * inicializa el estado de completado en falso
     * @param idLesson
     */
    public CourseProgress(String idLesson) {
        this.idLesson = idLesson;
        this.completeState=false;
    }
    /**Método que envía la id de la lección actual del curso
     * 
     * @return el id del la lección ej: LESS-2
     */

    public String getIdLesson() {
        return idLesson;
    }
    /**Método que modifca la id de la lección actual del curso
     * Usada para establecer el avance entre lecciones
     * @param idLesson id de la lección modificada ej: LESS-4
     */
    public void setIdLesson(String idLesson) {
        this.idLesson = idLesson;
    }

    /**Método que envía el estado de completado de un curso
     * 
     * @return el estado de completado de un curso falso o verdadero
     */
    public boolean isCompleteState() {
        return completeState;
    }

    /**Método que modifca el estado de completado de un curso
     * 
     * @param completeState estado de completado del curso, verdadero o falso
     */
    public void setCompleteState(boolean completeState) {
        this.completeState = completeState;
    }

    
    
}
