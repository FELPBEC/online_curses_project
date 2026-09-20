package co.edu.uptc.interfaces;

import java.util.List;
/**Interfaz que se contiene los contratos para cualquier tipo de repositorio que se quiera implementar
 * para almacenar los datos en persistencia 
 * @author @FELPBEC
 * @version v1.0
 * @since 19/09/2026
 * @param <T>
 */
public interface Repository<T> {
    void saveAll(List<T> objectList);
    List<T> sendAll();
}
