package co.edu.uptc.viewController;

import java.io.IOException;
import java.util.Locale;

import co.edu.uptc.exceptions.UserNotFoundException;
import co.edu.uptc.exceptions.WrongPasswordException;
import co.edu.uptc.view.App;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class TeacherLoginController {
    @FXML private ComboBox<String> languageSelector;
    @FXML private TextField txtIdentifier;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMessage;

    @FXML
    private void initialize() {
        languageSelector.getItems().setAll("Español", "English");
        languageSelector.setValue("es".equals(App.getMessages().getLocale().getLanguage())
                ? "Español" : "English");
    }

    @FXML
    private void onLogin() throws IOException {
        String identifier = txtIdentifier.getText().trim();
        String password = txtPassword.getText();
        if (identifier.isEmpty() || password.isEmpty()) {
            showMessage("teacher.login.error.required");
            return;
        }

        try {
            App.getGeneralController().teacherLogin(identifier, password);
            App.setRoot("teacher-menu");
        } catch (UserNotFoundException | WrongPasswordException e) {
            showMessage("teacher.login.error.credentials");
        }
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

    private void showMessage(String key) {
        lblMessage.setText(App.getMessages().getString(key));
    }
}
