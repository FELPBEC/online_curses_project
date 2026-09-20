package co.edu.uptc.model;

import java.util.List;
/**Clase TreeNode que sirve para empaquetar los elementos educativos en forma de nodos 
 * para poder manejarlos en el árbol
 * 
 * @author @FELPBEC
 * @version v1.0
 * @since 19/09/2026
 * 
 * @param <T>
 */
public class TreeNode<T> {
    T data;
    List<TreeNode<T>> sons;


}
