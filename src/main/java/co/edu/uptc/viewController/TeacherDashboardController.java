package co.edu.uptc.viewController;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.List;

import co.edu.uptc.model.Teacher;
import co.edu.uptc.view.App;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class TeacherDashboardController {
    @FXML private ComboBox<String> languageSelector;
    @FXML private Label lblWelcome;
    @FXML private Label lblCourseCount;
    @FXML private Label lblCourseList;

    @FXML
    private void initialize() {
        Teacher teacher = App.getGeneralController().getCurrentTeacher();
        if (teacher == null) {
            throw new IllegalStateException("A teacher must be authenticated before opening the menu.");
        }

        languageSelector.getItems().setAll("Español", "English");
        languageSelector.setValue("es".equals(App.getMessages().getLocale().getLanguage())
                ? "Español" : "English");
        lblWelcome.setText(MessageFormat.format(
                App.getMessages().getString("teacher.menu.welcome"), teacher.getUserName()));

        List<String> assignedCourses = teacher.getAsginedCourses();
        int courseCount = assignedCourses == null ? 0 : assignedCourses.size();
        lblCourseCount.setText(MessageFormat.format(
                App.getMessages().getString("teacher.menu.course.count"), courseCount));
        lblCourseList.setText(courseCount == 0
                ? App.getMessages().getString("teacher.menu.courses.empty")
                : String.join(", ", assignedCourses));
    }

    @FXML
    private void onLanguageChanged() throws IOException {
        String language = languageSelector.getValue();
        if (language != null) {
            App.setLocale("Español".equals(language) ? Locale.forLanguageTag("es") : Locale.ENGLISH);
        }
    }

    @FXML
    private void onLogout() throws IOException {
        App.getGeneralController().setCurrentTeacher(null);
        App.setRoot("role-selection");
    }

    @FXML
    private void onExit() {
        App.exitApplication();
    }
}
