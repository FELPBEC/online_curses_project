package co.edu.uptc.model;

import java.util.ArrayList;
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

    public TreeNode() {
        this.sons= new ArrayList<>();
    }

    public TreeNode(T data) {
        this.data = data;
        this.sons = new ArrayList<>();
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<TreeNode<T>> getSons() {
        return sons;
    }

    public void addSon(TreeNode<T> son) {
        this.sons.add(son);
    }

    

    
}
