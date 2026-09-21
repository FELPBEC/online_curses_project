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
    
    public Course() {
    }
    
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

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TreeNode<EducativeElement> getRoot() {
        return root;
    }

    public void setRoot(TreeNode<EducativeElement> root) {
        this.root = root;
    }

    public void setId(String id) {

        this.id = "COURSE-"+id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    
    
}