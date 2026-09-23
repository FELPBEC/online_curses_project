package co.edu.uptc.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Servicio de seguridad para contraseñas.
 * Responsabilidades:
 *   - Validar que la contraseña cumpla el formato mínimo
 *   - Encriptar con BCrypt
 *   - Verificar contraseña en texto plano contra hash almacenado
 *
 * Reglas de formato:
 *   - Mínimo 8 caracteres
 *   - Al menos una letra mayúscula
 *   - Al menos un carácter especial (. , ! @ # $ % & * - _ + = ? /)
 * @author @FELPBEC
 * @version v1.0
 * @since 21/09/2026
 */
public class PasswordSecurityService {

    // Regex: mínimo 8 chars, una mayúscula, un carácter especial
    private static final String PASSWORD_REGEX =
        "^(?=.*[A-Z])(?=.*[.\\,!@#$%&*\\-_+=?/]).{8,}$";

    /**
     * Valida que la contraseña en texto plano cumpla el formato requerido.
     *
     * @param rawPassword contraseña en texto plano
     * @return true si cumple las reglas, false si no
     */
    public boolean isValidFormat(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) return false;
        return rawPassword.matches(PASSWORD_REGEX);
    }

    /**
     * Encripta una contraseña en texto plano usando BCrypt con factor de costo 12.
     * SOLO llamar después de validar el formato con isValidFormat().
     *
     * @param rawPassword contraseña en texto plano
     * @return hash BCrypt listo para almacenar en JSON/BD
     */
    public String encrypt(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
    }

    /**
     * Verifica si una contraseña en texto plano corresponde al hash almacenado.
     * Úsalo en el login: nunca desencriptes, siempre compara así.
     *
     * @param rawPassword     contraseña que escribió el usuario
     * @param hashedPassword  hash almacenado en el JSON
     * @return true si coinciden
     */
    public boolean verify(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) return false;
        try {
            return BCrypt.checkpw(rawPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
