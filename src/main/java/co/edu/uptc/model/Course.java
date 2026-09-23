package co.edu.uptc.model;

import co.edu.uptc.interfaces.EducativeElementType;
import co.edu.uptc.interfaces.EducativeElement;
/**Clase de Cursos en linea que implementa la interfaz {@link EducativeElement}
 * 
 * @author @FELPBEC
 * @version v1.0
 * @since 19/09/2026
 */
public class Course implements EducativeElement{
    private String id;
    private String title;
    private TreeNode<EducativeElement> root;
    /**Método constructor vacío de la clase cursos para cargar cursos desde persistencia
     * 
     */
    public Course() {
    }
    /**Método constructor de la clase 
     * 
     * @param id
     * @param title
     * @param root
     */
    public Course(String id, String title, TreeNode<EducativeElement> root) {
        this.id = id;
        this.title = title;
        this.root = root;
    }

    /**Método que retorna el tipo de elemento educativo que es
     * 
     * 
     * @see co.edu.uptc.interfaces.EducativeElement#getElementType()
     * @return tipo de elemento educativo: COURSE
     */
    @Override
    public EducativeElementType getElementType() {
    
        return EducativeElementType.COURSE;
    }
    /**  Método que obtiene la id del curso
     * 
     * @see co.edu.uptc.interfaces.EducativeElement#getId()
     * @return la id del curso
     */
    public String getId() {
        return id;
    }

    /**  Método que obtiene el título del curso
     * 
     * @see co.edu.uptc.interfaces.EducativeElement#getTitle()
     */
    public String getTitle() {
        return title;
    }
    /**Método que envía el nodo raíz del curso
     * 
     * @return un nodo raíz
     */
    public TreeNode<EducativeElement> getRoot() {
        return root;
    }

    /**Método que establece el nodo raíz del curso
     * 
     * @param root raíz del curso 
     */
    public void setRoot(TreeNode<EducativeElement> root) {
        this.root = root;
    }
    /**Método que establece la id del curso
     * 
     * @param id id del curso
     */
    public void setId(String id) {

        this.id =id;
    }
    /**Método que establece el titulo del curso
     * 
     * @param title titulo del curso
     */

    public void setTitle(String title) {
        this.title = title;
    }

    
    
}