package co.edu.uptc.interfaces;

/**Intefaz de los elementos educativos,
 *  de tal forma que sea posible tratar a los 3 como NODOS en un árbol
 * 
 * @author @FELPBEC
 * @version v1.0
 * @since 19/09/2026
 */

public interface EducativeElement {
    EducativeElementType getElementType();
    String getId();
    String getTitle();
}
