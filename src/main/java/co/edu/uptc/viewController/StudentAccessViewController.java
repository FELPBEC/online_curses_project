package co.edu.uptc.viewController;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import co.edu.uptc.exceptions.CredentialsAlreadyExistException;
import co.edu.uptc.exceptions.InvalidFortmatException;
import co.edu.uptc.exceptions.SavedFailureException;
import co.edu.uptc.exceptions.UserNotFoundException;
import co.edu.uptc.exceptions.WrongPasswordException;
import co.edu.uptc.view.App;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class StudentAccessViewController {
    private static final Logger LOGGER = Logger.getLogger(StudentAccessViewController.class.getName());
    private static final String EMAIL_REGEX = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";

    @FXML private VBox registerFields;
    @FXML private VBox confirmFields;
    @FXML private TextField txtUserName;
    @FXML private TextField txtIdentifier;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Label lblTitle;
    @FXML private Label lblSubtitle;
    @FXML private Label lblMessage;
    @FXML private javafx.scene.control.Button btnPrimary;
    @FXML private Hyperlink btnSwitchMode;
    @FXML private ComboBox<String> languageSelector;

    private boolean registrationMode;

    @FXML
    private void initialize() {
        languageSelector.getItems().setAll("Español", "English");
        languageSelector.setValue("es".equals(App.getMessages().getLocale().getLanguage())
                ? "Español" : "English");
        updateMode();
    }

    @FXML
    private void onPrimaryAction() throws IOException {
        if (registrationMode) {
            registerStudent();
        } else {
            loginStudent();
        }
    }

    @FXML
    private void onSwitchMode() {
        registrationMode = !registrationMode;
        lblMessage.setText("");
        txtUserName.clear();
        txtIdentifier.clear();
        txtPassword.clear();
        txtConfirmPassword.clear();
        updateMode();
    }

    @FXML
    private void onBack() throws IOException {
        App.setRoot("role-selection");
    }

    @FXML
    private void onLanguageChanged() throws IOException {
        String language = languageSelector.getValue();
        if (language != null) {
            App.setLocale("Español".equals(language) ? Locale.forLanguageTag("es") : Locale.ENGLISH);
        }
    }

    private void loginStudent() throws IOException {
        String identifier = txtIdentifier.getText().trim();
        String password = txtPassword.getText();
        if (identifier.isEmpty() || password.isEmpty()) {
            showMessage("login.error.required");
            return;
        }

        try {
            App.getGeneralController().estudentLogin(identifier, password);
            App.setRoot("student-menu");
        } catch (UserNotFoundException | WrongPasswordException e) {
            showMessage("login.error.credentials");
        }
    }

    private void registerStudent() throws IOException {
        String userName = txtUserName.getText().trim();
        String email = txtIdentifier.getText().trim();
        String password = txtPassword.getText();
        if (userName.isEmpty() || email.isEmpty() || password.isEmpty()
                || txtConfirmPassword.getText().isEmpty()) {
            showMessage("login.error.required");
            return;
        }
        if (!email.matches(EMAIL_REGEX)) {
            showMessage("login.error.email");
            return;
        }
        if (!password.equals(txtConfirmPassword.getText())) {
            showMessage("login.error.password.mismatch");
            return;
        }

        try {
            App.getGeneralController().registerEstudent(userName, email, password);
            App.setRoot("student-menu");
        } catch (InvalidFortmatException e) {
            showMessage("login.error.password.format");
        } catch (CredentialsAlreadyExistException e) {
            showMessage("login.error.duplicate");
        } catch (SavedFailureException e) {
            LOGGER.log(Level.SEVERE, "Could not save the new student account.", e);
            showMessage("login.error.persistence");
        }
    }

    private void updateMode() {
        ResourceBundle messages = App.getMessages();
        lblTitle.setText(messages.getString(registrationMode ? "login.title.register" : "login.title"));
        lblSubtitle.setText(messages.getString(registrationMode
                ? "login.subtitle.register" : "login.subtitle"));
        btnPrimary.setText(messages.getString(registrationMode
                ? "login.action.register" : "login.action.login"));
        btnSwitchMode.setText(messages.getString(registrationMode
                ? "login.switch.login" : "login.switch.register"));
        registerFields.setVisible(registrationMode);
        registerFields.setManaged(registrationMode);
        confirmFields.setVisible(registrationMode);
        confirmFields.setManaged(registrationMode);
        txtIdentifier.setPromptText(messages.getString(
                registrationMode ? "login.email" : "login.identifier"));
    }

    private void showMessage(String messageKey) {
        lblMessage.setText(App.getMessages().getString(messageKey));
    }
}
