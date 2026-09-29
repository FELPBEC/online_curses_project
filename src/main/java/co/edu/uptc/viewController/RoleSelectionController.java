package co.edu.uptc.viewController;

import java.io.IOException;
import java.util.Locale;

import co.edu.uptc.view.App;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class RoleSelectionController {
    @FXML private ComboBox<String> languageSelector;

    @FXML
    private void initialize() {
        languageSelector.getItems().setAll("Español", "English");
        languageSelector.setValue("es".equals(App.getMessages().getLocale().getLanguage())
                ? "Español" : "English");
    }

    @FXML
    private void onStudentSelected() throws IOException {
        App.setRoot("student-access");
    }

    @FXML
    private void onTeacherSelected() throws IOException {
        App.setRoot("teacher-access");
    }

    @FXML
    private void onExit() {
        App.exitApplication();
    }

    @FXML
    private void onLanguageChanged() throws IOException {
        String language = languageSelector.getValue();
        if (language != null) {
            App.setLocale("Español".equals(language) ? Locale.forLanguageTag("es") : Locale.ENGLISH);
        }
    }
}
