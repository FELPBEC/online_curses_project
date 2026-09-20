package co.edu.uptc.abstracts;
/**Clase abstracta User como molde para las clases de los tipos de usuarios que interactúan con la aplicación
 * 
 * @author Felipe Becerra
 * @version v1.0
 * @since 19/09/2026
 */
public abstract class User{
    protected int id;
    protected String userName;
    protected String email;
    protected String password;
    /**Constructor vacío de la clase User para poder almacenar los usaurios en persistencia
     * 
     */
    public User() {
    }
    /**Método constructor con párametros para crear un usuario manualmente
     *  
     * @param userName nombre de usuario de tipo String
     * @param email correo électronico del usuario
     * @param password  contraseña del usuario 
     * @param id    identificador númerico del usuario
     */
    public User(String userName, String email, String password,int id) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.id=id;
    }
    /**Método para obtener el nombre de usuario
     * 
     * @return  nombre de usuario
     */
    public String getUserName() {
        return userName;
    }
    /**Método para modificar el nombre de usuario
     * 
     * @param userName  nuevo nombre de usuario
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    /**Método para obtener el email del usuario
     * 
     * @return  email del usuario
     */
    public String getEmail() {
        return email;
    }
    /**Método para modificar el email del usuario
     * 
     * @param email nuevo email del usuario
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**Método para obtener la contraseña del usuario
     * 
     * @return  contraseña del usuario
     */
    public String getPassword() {
        return password;
    }  
    /**Método para modificar la contraseña del usuario
     * 
     * @param password  nueva contraseña del usuario
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**Método para obtener la id del usuario
     * 
     * @return id del usuario
     */
    public int getId() {
        return id;
    }
    /**Método para modificar la id del usuario
     * 
     * @param id  nueva id del usuario
     */
    public void setId(int id) {
        this.id = id;
    }
    /**Método que compara unas credenciales inscritas con las almacenadas de un usuario 
     * 
     * @param insertEmail   email ingresado    
     * @param insertPassword    contraseña ingresada
     * @return  verdadero si las creedenciales coinciden y falso si no
     */
    public boolean validateCredentials(String insertEmail, String insertPassword){
        if ((insertEmail==email)&&(insertPassword==password)) {
            return true;
        }else{
            return false;
        }
    }
    
}