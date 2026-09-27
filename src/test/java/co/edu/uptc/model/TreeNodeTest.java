package co.edu.uptc.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para la estructura genérica {@link TreeNode}.
 */
public class TreeNodeTest {

    @Test
    @DisplayName("Debe almacenar el dato genérico y permitir añadir hijos")
    public void testAddSonAndGetSons() {
        TreeNode<String> rootNode = new TreeNode<>("Nodo Raíz");
        TreeNode<String> childNode1 = new TreeNode<>("Hijo 1");
        TreeNode<String> childNode2 = new TreeNode<>("Hijo 2");

        rootNode.addSon(childNode1);
        rootNode.addSon(childNode2);

        assertEquals("Nodo Raíz", rootNode.getData());
        assertEquals(2, rootNode.getSons().size());
        assertEquals("Hijo 1", rootNode.getSons().get(0).getData());
        assertEquals("Hijo 2", rootNode.getSons().get(1).getData());
    }

    @Test
    @DisplayName("Debe permitir establecer y modificar el dato almacenado mediante setData")
    public void testSetDataAndConstructors() {
        TreeNode<String> node = new TreeNode<>();
        assertNull(node.getData());

        node.setData("Nuevo Dato");
        assertEquals("Nuevo Dato", node.getData());
    }
}