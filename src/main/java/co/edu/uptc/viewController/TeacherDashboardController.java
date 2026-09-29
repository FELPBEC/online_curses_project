package co.edu.uptc.viewController;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import co.edu.uptc.exceptions.SavedFailureException;
import co.edu.uptc.interfaces.EducativeElement;
import co.edu.uptc.interfaces.EducativeElementType;
import co.edu.uptc.model.Course;
import co.edu.uptc.model.Lessons;
import co.edu.uptc.model.Teacher;
import co.edu.uptc.model.TreeNode;
import co.edu.uptc.view.App;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class TeacherDashboardController {
    private static final Logger LOGGER = Logger.getLogger(TeacherDashboardController.class.getName());

    @FXML private ComboBox<String> languageSelector;
    @FXML private Label lblWelcome;
    @FXML private Label lblCourseCount;
    @FXML private Label lblMessage;
    @FXML private TextField txtCourseTitle;
    @FXML private TextArea txtCourseDescription;
    @FXML private Button btnMyCourses;
    @FXML private Button btnCreateNewCourse;
    @FXML private VBox coursesView;
    @FXML private VBox courseListHost;
    @FXML private Label lblEmptyCourses;
    @FXML private VBox courseDetailView;
    @FXML private Label lblSelectedCourseTitle;
    @FXML private Label lblSelectedCourseId;
    @FXML private Label lblSelectedCourseDescription;
    @FXML private Label lblNodeTitle;
    @FXML private Label lblNodeType;
    @FXML private Label lblNodeDescription;
    @FXML private Button btnDeleteNode;
    @FXML private Button btnCreateModule;
    @FXML private Button btnCreateLesson;
    @FXML private VBox createModuleForm;
    @FXML private VBox createLessonForm;
    @FXML private TextField txtModuleTitle;
    @FXML private TextArea txtModuleDescription;
    @FXML private TextField txtLessonTitle;
    @FXML private TextArea txtLessonDescription;
    @FXML private TextField txtLessonDuration;
    @FXML private Label lblNodeActionMessage;
    @FXML private StackPane courseTreeHost;
    @FXML private VBox createCourseView;

    private CoursesListController coursesListController;
    private CourseTreeView courseTreeView;
    private Course selectedCourse;
    private TreeNode<EducativeElement> selectedNode;
    @FXML
    private Button btnToggleMenu;
    @FXML
    private HBox menuHoja;
    @FXML
    private VBox contenedorMenuAnimado;
    private TranslateTransition animacion;
    private boolean menuAbierto = false;
    @FXML
    private void initialize() {
        contenedorMenuAnimado.setTranslateY(250);
        Teacher teacher = App.getGeneralController().getCurrentTeacher();
        if (teacher == null) {
            throw new IllegalStateException("A teacher must be authenticated before opening the menu.");
        }

        languageSelector.getItems().setAll("Español", "English");
        languageSelector.setValue("es".equals(App.getMessages().getLocale().getLanguage())
                ? "Español" : "English");
        lblWelcome.setText(MessageFormat.format(
                App.getMessages().getString("teacher.menu.welcome"), teacher.getUserName()));
        coursesListController = new CoursesListController();
        coursesListController.setOnCourseSelected(this::showCourseDetails);
        courseListHost.getChildren().setAll(coursesListController);
        showCourses();
    }

    @FXML 
    public void toggleMenu() {
    if (animacion != null && animacion.getStatus() == javafx.animation.Animation.Status.RUNNING) {
        return; 
    }

    // Animar el VBox completo
    animacion = new TranslateTransition(Duration.millis(350), contenedorMenuAnimado);

    if (menuAbierto) {
        animacion.setToY(250); // Bajar (esconde la hoja, deja el botón visible)
        menuAbierto = false;
    } else {
        animacion.setToY(0);   // Subir (muestra todo en su posición normal)
        menuAbierto = true;
    }

    animacion.play();
}
    @FXML
    private void onShowCourses() {
        showCourses();
    }

    @FXML
    private void onCreateNewCourse() {
        coursesView.setVisible(false);
        coursesView.setManaged(false);
        createCourseView.setVisible(true);
        createCourseView.setManaged(true);
        lblMessage.setText("");
        lblMessage.setVisible(false);
        lblMessage.setManaged(false);
        btnMyCourses.getStyleClass().setAll("boton-dibujado");
        btnCreateNewCourse.getStyleClass().setAll(
                "boton-dibujado", "boton-seleccionado");
    }

    @FXML
    private void onSaveNewCourse() {
        String title = txtCourseTitle.getText().trim();
        String description = txtCourseDescription.getText().trim();
        if (title.isEmpty() || description.isEmpty()) {
            showMessage("teacher.course.create.required", false);
            return;
        }

        try {
            App.getGeneralController().createCourseForCurrentTeacher(title, description);
            txtCourseTitle.clear();
            txtCourseDescription.clear();
            refreshCourses();
            showMessage("teacher.course.create.success", true);
        } catch (SavedFailureException e) {
            LOGGER.log(Level.SEVERE, "Could not save the new course.", e);
            showMessage("teacher.course.create.saveError", false);
        }
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

    private void showCourses() {
        createCourseView.setVisible(false);
        createCourseView.setManaged(false);
        courseDetailView.setVisible(false);
        courseDetailView.setManaged(false);
        coursesView.setVisible(true);
        coursesView.setManaged(true);
        btnMyCourses.getStyleClass().setAll(
                "boton-dibujado", "boton-seleccionado");
        btnCreateNewCourse.getStyleClass().setAll("boton-dibujado");
        refreshCourses();
    }

    @FXML
    private void onBackToCourses() {
        selectedCourse = null;
        selectedNode = null;
        coursesListController.clearSelectedCourse();
        showCourses();
    }

    @FXML
    private void onCreateModule() {
        createModuleForm.setVisible(true);
        createModuleForm.setManaged(true);
        createLessonForm.setVisible(false);
        createLessonForm.setManaged(false);
        clearNodeActionMessage();
    }

    @FXML
    private void onCreateLesson() {
        createModuleForm.setVisible(false);
        createModuleForm.setManaged(false);
        createLessonForm.setVisible(true);
        createLessonForm.setManaged(true);
        clearNodeActionMessage();
    }

    @FXML
    private void onCancelNodeCreation() {
        hideNodeCreationForms();
        clearNodeActionMessage();
    }

    @FXML
    private void onDeleteSelectedNode() {
        if (selectedCourse == null || selectedNode == null || selectedNode.getData() == null) {
            return;
        }

        EducativeElement element = selectedNode.getData();
        String title = element.getTitle() == null ? element.getId() : element.getTitle();
        String confirmationKey = element.getElementType() == EducativeElementType.COURSE
                ? "teacher.node.delete.confirm.course"
                : "teacher.node.delete.confirm.message";
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle(App.getMessages().getString("teacher.node.delete.confirm.title"));
        confirmation.setHeaderText(null);
        confirmation.setContentText(MessageFormat.format(
                App.getMessages().getString(confirmationKey), title));
        ButtonType deleteButton = new ButtonType(
                App.getMessages().getString("teacher.node.delete.confirm.action"),
                ButtonType.OK.getButtonData());
        ButtonType cancelButton = new ButtonType(
                App.getMessages().getString("teacher.node.delete.confirm.cancel"),
                ButtonType.CANCEL.getButtonData());
        confirmation.getButtonTypes().setAll(deleteButton, cancelButton);
        if (!confirmation.showAndWait().filter(deleteButton::equals).isPresent()) {
            return;
        }

        App.getGeneralController().setCurrentCourse(selectedCourse);
        switch (element.getElementType()) {
            case COURSE:
                App.getGeneralController().removeCurrentCourse();
                coursesListController.clearSelectedCourse();
                selectedCourse = null;
                selectedNode = null;
                App.getGeneralController().setCurrentCourse(null);
                showCourses();
                break;
            case MODULO:
                App.getGeneralController().removeModule(element.getId());
                refreshCourseTree(selectedCourse.getId());
                showNodeActionMessage("teacher.node.delete.success", true);
                break;
            case LESSON:
                App.getGeneralController().removeLesson(element.getId());
                refreshCourseTree(selectedCourse.getId());
                showNodeActionMessage("teacher.node.delete.success", true);
                break;
            default:
                throw new IllegalStateException("Unsupported educational element type: "
                        + element.getElementType());
        }
    }

    @FXML
    private void onSaveModule() {
        if (selectedCourse == null || selectedNode == null) {
            return;
        }
        String title = txtModuleTitle.getText().trim();
        String description = txtModuleDescription.getText().trim();
        if (title.isEmpty() || description.isEmpty()) {
            showNodeActionMessage("teacher.node.create.required", false);
            return;
        }

        try {
            String parentId = selectedNode.getData().getId();
            App.getGeneralController().addModuleToCurrentTeacherCourse(
                    selectedCourse.getId(), parentId, title, description);
            txtModuleTitle.clear();
            txtModuleDescription.clear();
            refreshCourseTree(parentId);
            hideNodeCreationForms();
            showNodeActionMessage("teacher.node.create.success", true);
        } catch (SavedFailureException e) {
            LOGGER.log(Level.SEVERE, "Could not save the new course module.", e);
            showNodeActionMessage("teacher.node.create.saveError", false);
        }
    }

    @FXML
    private void onSaveLesson() {
        if (selectedCourse == null || selectedNode == null) {
            return;
        }
        String title = txtLessonTitle.getText().trim();
        String description = txtLessonDescription.getText().trim();
        String durationText = txtLessonDuration.getText().trim();
        if (title.isEmpty() || description.isEmpty() || durationText.isEmpty()) {
            showNodeActionMessage("teacher.node.create.required", false);
            return;
        }

        double duration;
        try {
            duration = Double.parseDouble(durationText);
        } catch (NumberFormatException e) {
            showNodeActionMessage("teacher.node.create.invalidDuration", false);
            return;
        }
        if (!Double.isFinite(duration) || duration <= 0) {
            showNodeActionMessage("teacher.node.create.invalidDuration", false);
            return;
        }

        try {
            String parentId = selectedNode.getData().getId();
            App.getGeneralController().addLessonToCurrentTeacherCourse(
                    selectedCourse.getId(), parentId, title, description, duration);
            txtLessonTitle.clear();
            txtLessonDescription.clear();
            txtLessonDuration.clear();
            refreshCourseTree(parentId);
            hideNodeCreationForms();
            showNodeActionMessage("teacher.node.create.success", true);
        } catch (SavedFailureException e) {
            LOGGER.log(Level.SEVERE, "Could not save the new course lesson.", e);
            showNodeActionMessage("teacher.node.create.saveError", false);
        }
    }

    private void showCourseDetails(Course course) {
        selectedCourse = course;
        lblSelectedCourseTitle.setText(course.getTitle());
        lblSelectedCourseId.setText(MessageFormat.format(
                App.getMessages().getString("catalog.course.id"), course.getId()));
        String description = course.getDescription();
        lblSelectedCourseDescription.setText(description == null || description.isBlank()
                ? App.getMessages().getString("catalog.course.noDescription") : description);

        courseTreeView = new CourseTreeView();
        courseTreeView.setOnNodeSelectedListener(this::showNodeDetails);
        courseTreeView.render(course.getRoot());
        courseTreeHost.getChildren().setAll(courseTreeView);
        showNodeDetails(null);

        coursesView.setVisible(false);
        coursesView.setManaged(false);
        createCourseView.setVisible(false);
        createCourseView.setManaged(false);
        courseDetailView.setVisible(true);
        courseDetailView.setManaged(true);
    }

    private void showNodeDetails(TreeNode<EducativeElement> node) {
        selectedNode = node;
        hideNodeCreationForms();
        clearNodeActionMessage();
        if (node == null || node.getData() == null) {
            lblNodeTitle.setText(App.getMessages().getString("catalog.node.select"));
            lblNodeType.setText("");
            lblNodeDescription.setText("");
            btnDeleteNode.setVisible(false);
            btnDeleteNode.setManaged(false);
            btnCreateModule.setVisible(false);
            btnCreateModule.setManaged(false);
            btnCreateLesson.setVisible(false);
            btnCreateLesson.setManaged(false);
            return;
        }

        EducativeElement element = node.getData();
        lblNodeTitle.setText(element.getTitle() == null ? element.getId() : element.getTitle());
        lblNodeType.setText(App.getMessages().getString(nodeTypeMessageKey(element.getElementType())));
        String description = element.getDescription() == null || element.getDescription().isBlank()
                ? App.getMessages().getString("catalog.course.noDescription")
                : element.getDescription();
        if (element instanceof Lessons) {
            description += System.lineSeparator() + MessageFormat.format(
                    App.getMessages().getString("catalog.node.duration"),
                    ((Lessons) element).getDuration());
        }
        lblNodeDescription.setText(description);
        btnDeleteNode.setVisible(true);
        btnDeleteNode.setManaged(true);
        boolean canCreateModule = element.getElementType() == EducativeElementType.COURSE
                || element.getElementType() == EducativeElementType.MODULO;
        boolean canCreateLesson = element.getElementType() == EducativeElementType.MODULO;
        btnCreateModule.setVisible(canCreateModule);
        btnCreateModule.setManaged(canCreateModule);
        btnCreateLesson.setVisible(canCreateLesson);
        btnCreateLesson.setManaged(canCreateLesson);
    }

    private void refreshCourseTree(String selectedNodeId) {
        courseTreeView.render(selectedCourse.getRoot());
        courseTreeView.selectNodeById(selectedNodeId);
    }

    private void hideNodeCreationForms() {
        createModuleForm.setVisible(false);
        createModuleForm.setManaged(false);
        createLessonForm.setVisible(false);
        createLessonForm.setManaged(false);
    }

    private void clearNodeActionMessage() {
        lblNodeActionMessage.setText("");
        lblNodeActionMessage.setVisible(false);
        lblNodeActionMessage.setManaged(false);
    }

    private void showNodeActionMessage(String messageKey, boolean success) {
        lblNodeActionMessage.getStyleClass().setAll(
                success ? "course-success-message" : "auth-message");
        lblNodeActionMessage.setText(App.getMessages().getString(messageKey));
        lblNodeActionMessage.setVisible(true);
        lblNodeActionMessage.setManaged(true);
    }

    private String nodeTypeMessageKey(EducativeElementType type) {
        if (type == EducativeElementType.LESSON) {
            return "catalog.node.lesson";
        }
        if (type == EducativeElementType.MODULO) {
            return "catalog.node.module";
        }
        return "catalog.node.course";
    }

    private void refreshCourses() {
        List<Course> assignedCourses = getAssignedCourses();
        coursesListController.DrawCoursesList(assignedCourses);
        lblCourseCount.setText(MessageFormat.format(
                App.getMessages().getString("teacher.menu.course.count"), assignedCourses.size()));
        lblEmptyCourses.setVisible(assignedCourses.isEmpty());
        lblEmptyCourses.setManaged(assignedCourses.isEmpty());
    }

    private List<Course> getAssignedCourses() {
        List<Course> assignedCourses = new ArrayList<>();
        List<String> assignedCourseIds = App.getGeneralController()
                .getCurrentTeacher().getAsginedCourses();
        if (assignedCourseIds == null) {
            return assignedCourses;
        }
        for (Course course : App.getGeneralController().getCourseList()) {
            if (assignedCourseIds.contains(course.getId())) {
                assignedCourses.add(course);
            }
        }
        return assignedCourses;
    }

    private void showMessage(String messageKey, boolean success) {
        lblMessage.getStyleClass().setAll(
                success ? "course-success-message" : "auth-message");
        lblMessage.setText(App.getMessages().getString(messageKey));
        lblMessage.setVisible(true);
        lblMessage.setManaged(true);
    }
}
