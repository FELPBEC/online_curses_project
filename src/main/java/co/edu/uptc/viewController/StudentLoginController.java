package co.edu.uptc.viewController;

import java.io.IOException;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import co.edu.uptc.model.Estudent;
import co.edu.uptc.persistence.EstudentJsonRepository;
import co.edu.uptc.util.PasswordSecurityService;
import co.edu.uptc.view.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**Controlador de la vista de login / registro del estudiante.
 * Una sola vista con dos modos: iniciar sesión y registrarse.
 */
public class StudentLoginController {

    private static final String STUDENTS_PATH = "Estudents.json";
    private static final String EMAIL_REGEX = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$";
    private static final String LOGIN_VIEW = "/co/edu/uptc/view/StudentLogin.fxml";
    private static final String MENU_VIEW = "/co/edu/uptc/view/StudentMenu.fxml"; // aún no existe

    // JavaFX lo inyecta solo: es el bundle con el que se cargó el FXML
    @FXML private ResourceBundle resources;

    @FXML private Label lblTitle;
    @FXML private Label lblMessage;
    @FXML private VBox registerFields;
    @FXML private VBox confirmFields;
    @FXML private TextField txtUserName;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Button btnPrimary;
    @FXML private Button btnSwitchMode;

    private final PasswordSecurityService passwordService = new PasswordSecurityService();
    private final EstudentJsonRepository repository = new EstudentJsonRepository(STUDENTS_PATH);
    private boolean registerMode = false;

    @FXML
    private void initialize() {
        applyMode();
    }

    /**Botón principal: según el modo, inicia sesión o registra */
    @FXML
    private void onPrimaryAction() {
        if (registerMode) {
            register();
        } else {
            login();
        }
    }

    /**Alterna entre login y registro */
    @FXML
    private void onSwitchMode() {
        registerMode = !registerMode;
        applyMode();
    }

    /**Cambia el idioma y recarga la vista */
    @FXML
    private void onToggleLanguage() {
        Locale locale = "es".equals(App.getMessages().getLocale().getLanguage())
                ? Locale.ENGLISH : Locale.forLanguageTag("es");
        try {
            App.setLocale(locale);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LOGIN_VIEW), App.getMessages());
            Parent root = loader.load();
            btnPrimary.getScene().setRoot(root);
        } catch (IOException e) {
            showError("error.load");
        }
    }

    private void login() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("error.empty");
            return;
        }
        Estudent student = findByEmail(email);
        // Mismo mensaje si el correo no existe o la contraseña falla (no revela cuál)
        if (student == null || !passwordService.verify(password, student.getPassword())) {
            showError("error.credentials");
            return;
        }
        openStudentMenu(student);
    }

    private void register() {
        String userName = txtUserName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        String confirm = txtConfirmPassword.getText();

        if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showError("error.empty");
            return;
        }
        if (!email.matches(EMAIL_REGEX)) {
            showError("error.email.format");
            return;
        }
        if (!passwordService.isValidFormat(password)) {
            showError("error.password.format");
            return;
        }
        if (!password.equals(confirm)) {
            showError("error.password.mismatch");
            return;
        }
        if (findByEmail(email) != null) {
            showError("error.email.exists");
            return;
        }

        List<Estudent> students = loadStudents();
        int newId = students.stream().mapToInt(Estudent::getId).max().orElse(0) + 1;
        // Se guarda el hash, nunca la contraseña en texto plano
        Estudent student = new Estudent(newId, userName, email, passwordService.encrypt(password));
        students.add(student);
        repository.saveAll(students);

        openStudentMenu(student);
    }

    private Estudent findByEmail(String email) {
        return loadStudents().stream()
                .filter(s -> s.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    private List<Estudent> loadStudents() {
        List<Estudent> list = repository.sendAll();
        return list == null ? new ArrayList<>() : new ArrayList<>(list);
    }

    /**Navega al menú del estudiante (pendiente de crear) */
    private void openStudentMenu(Estudent student) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(MENU_VIEW), App.getMessages());
            Parent root = loader.load();
            // TODO: cuando exista el StudentMenuController, pasarle el estudiante:
            // loader.<StudentMenuController>getController().setStudent(student);
            btnPrimary.getScene().setRoot(root);
        } catch (IOException | RuntimeException e) {
            showError("error.load");
        }
    }

    /**Ajusta textos y campos visibles según el modo actual */
    private void applyMode() {
        registerFields.setVisible(registerMode);
        registerFields.setManaged(registerMode);
        confirmFields.setVisible(registerMode);
        confirmFields.setManaged(registerMode);

        lblTitle.setText(resources.getString(registerMode ? "register.title" : "login.title"));
        btnPrimary.setText(resources.getString(registerMode ? "register.button" : "login.button"));
        btnSwitchMode.setText(resources.getString(registerMode ? "register.switch" : "login.switch"));
        lblMessage.setText("");
    }

    private void showError(String key) {
        lblMessage.setText(resources.getString(key));
    }
}
