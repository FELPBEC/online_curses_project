package co.edu.uptc.viewController;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import co.edu.uptc.exceptions.NoAvaliableLessonsInTheCourseException;
import co.edu.uptc.exceptions.SavedFailureException;
import co.edu.uptc.interfaces.EducativeElement;
import co.edu.uptc.interfaces.EducativeElementType;
import co.edu.uptc.model.Course;
import co.edu.uptc.model.Estudent;
import co.edu.uptc.model.Lessons;
import co.edu.uptc.model.TreeNode;
import co.edu.uptc.view.App;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class StudentDashboardController {
    @FXML private ComboBox<String> languageSelector;
    @FXML private Label lblWelcome;
    @FXML private Label lblCourseCount;
    @FXML private Label lblCatalogTitle;
    @FXML private Label lblEmptyCourses;
    @FXML private Label lblSelectedCourseTitle;
    @FXML private Label lblSelectedCourseId;
    @FXML private Label lblSelectedCourseDescription;
    @FXML private Label lblEnrollmentMessage;
    @FXML private Label lblNodeTitle;
    @FXML private Label lblNodeType;
    @FXML private Label lblNodeDescription;
    @FXML private Label lblStatisticsEnrolled;
    @FXML private Label lblStatisticsCompleted;
    @FXML private Label lblStatisticsAverage;
    @FXML private Label lblStatisticsEmpty;
    @FXML private Label lblChartTitle;
    @FXML private TextField txtCourseSearch;
    @FXML private Button btnToggleChart;
    @FXML private Button btnCatalog;
    @FXML private Button btnMyCourses;
    @FXML private Button btnEnroll;
    @FXML private Button btnCompleteLesson;
    @FXML private Button btnStatistics;
    @FXML private VBox catalogView;
    @FXML private VBox courseDetailView;
    @FXML private VBox statisticsView;
    @FXML private VBox courseListHost;
    @FXML private VBox nodeDetailsPanel;
    @FXML private StackPane courseTreeHost;
    @FXML private PieChart courseCompletionChart;
    @FXML private BarChart<String, Number> courseProgressChart;
    private TranslateTransition animacion;
    private boolean menuAbierto = false;
    private CoursesListController coursesListController;
    private CourseTreeView courseTreeView;
    private Course selectedCourse;
    private boolean showingEnrolledCourses;
    private boolean selectedCourseIsEnrolled;
    private boolean showingPieChart;
    
    @FXML
    private Button btnToggleMenu;
    @FXML
    private HBox menuHoja;
    @FXML
    private VBox contenedorMenuAnimado;

    @FXML
    private void initialize() {
        contenedorMenuAnimado.setTranslateY(260);
        Estudent student = App.getGeneralController().getCurrentEstudent();
        if (student == null) {
            throw new IllegalStateException("A student must be authenticated before opening the menu.");
        }

        languageSelector.getItems().setAll("Español", "English");
        languageSelector.setValue("es".equals(App.getMessages().getLocale().getLanguage())
                ? "Español" : "English");
        btnCatalog.getStyleClass().setAll("boton-dibujado");
        btnMyCourses.getStyleClass().setAll("boton-dibujado");
        btnStatistics.getStyleClass().setAll("boton-dibujado");
        btnToggleChart.getStyleClass().setAll("boton-dibujado");
        lblWelcome.setText(MessageFormat.format(
                App.getMessages().getString("menu.welcome"), student.getUserName()));

        courseCompletionChart.setLabelsVisible(false);
        courseCompletionChart.setLegendVisible(true);
        courseCompletionChart.setVisible(false);
        courseCompletionChart.setManaged(false);
        updateChartView();

        coursesListController = new CoursesListController();
        coursesListController.setOnCourseSelected(this::showCourseDetails);
        courseListHost.getChildren().setAll(coursesListController);
        txtCourseSearch.textProperty().addListener((observable, previous, current) -> refreshCourseCards());
        refreshCourseCards();
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
    private void onShowCatalog() {
        showingEnrolledCourses = false;
        selectedCourse = null;
        coursesListController.clearSelectedCourse();
        statisticsView.setVisible(false);
        statisticsView.setManaged(false);
        courseDetailView.setVisible(false);
        courseDetailView.setManaged(false);
        catalogView.setVisible(true);
        catalogView.setManaged(true);
        txtCourseSearch.clear();
        refreshCourseCards();
    }

    @FXML
    private void onShowMyCourses() {
        showingEnrolledCourses = true;
        selectedCourse = null;
        coursesListController.clearSelectedCourse();
        statisticsView.setVisible(false);
        statisticsView.setManaged(false);
        courseDetailView.setVisible(false);
        courseDetailView.setManaged(false);
        catalogView.setVisible(true);
        catalogView.setManaged(true);
        txtCourseSearch.clear();
        refreshCourseCards();
    }

    @FXML
    private void onShowStatistics() {
        catalogView.setVisible(false);
        catalogView.setManaged(false);
        courseDetailView.setVisible(false);
        courseDetailView.setManaged(false);
        statisticsView.setVisible(true);
        statisticsView.setManaged(true);
        refreshStatistics();
    }

    @FXML
    private void onToggleChart() {
        showingPieChart = !showingPieChart;
        updateChartView();
    }

    @FXML
    private void onBackToCourses() {
        statisticsView.setVisible(false);
        statisticsView.setManaged(false);
        courseDetailView.setVisible(false);
        courseDetailView.setManaged(false);
        catalogView.setVisible(true);
        catalogView.setManaged(true);
        selectedCourse = null;
        coursesListController.clearSelectedCourse();
        refreshCourseCards();
    }

    @FXML
    private void onEnroll() {
        if (selectedCourse == null) {
            return;
        }
        try {
            boolean enrolled = App.getGeneralController()
                    .registerCurrentStudentOnCourse(selectedCourse.getId());
            selectedCourseIsEnrolled = enrolled
                    || App.getGeneralController().getCurrentEstudent()
                            .isRegisterOnCourse(selectedCourse.getId());
            updateEnrollmentState(enrolled
                    ? App.getMessages().getString("catalog.enrollment.success")
                    : App.getMessages().getString("catalog.enrollment.already"), true);
            String currentLessonId = App.getGeneralController()
                    .getCurrentLessonId(selectedCourse.getId());
            courseTreeView.setHighlightedNodeId(currentLessonId);
            showNodeDetails(null);
            btnEnroll.setVisible(false);
            btnEnroll.setManaged(false);
            refreshCourseCount();
            refreshCourseCards();
        } catch (NoAvaliableLessonsInTheCourseException e) {
            updateEnrollmentState(App.getMessages().getString("catalog.enrollment.noLessons"), false);
        } catch (SavedFailureException e) {
            updateEnrollmentState(App.getMessages().getString("catalog.enrollment.persistenceError"), false);
        }
    }

    @FXML
    private void onCompleteLesson() {
        if (selectedCourse == null || !selectedCourseIsEnrolled
                || !btnCompleteLesson.isVisible()) {
            return;
        }
        try {
            boolean courseCompleted = App.getGeneralController()
                    .completeCurrentStudentLesson(selectedCourse.getId());
            refreshCourseCount();
            if (courseCompleted) {
                courseTreeView.setHighlightedNodeId(null);
                btnCompleteLesson.setVisible(false);
                btnCompleteLesson.setManaged(false);
                lblEnrollmentMessage.setText(App.getMessages().getString("catalog.course.completed"));
                lblEnrollmentMessage.setVisible(true);
                lblEnrollmentMessage.setManaged(true);
            } else {
                String currentLessonId = App.getGeneralController()
                        .getCurrentLessonId(selectedCourse.getId());
                courseTreeView.setHighlightedNodeId(currentLessonId);
                lblEnrollmentMessage.setText(App.getMessages().getString("catalog.lesson.advanced"));
                lblEnrollmentMessage.setVisible(true);
                lblEnrollmentMessage.setManaged(true);
                showNodeDetails(null);
            }
        } catch (SavedFailureException e) {
            lblEnrollmentMessage.setText(App.getMessages().getString("catalog.lesson.persistenceError"));
            lblEnrollmentMessage.setVisible(true);
            lblEnrollmentMessage.setManaged(true);
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
        App.getGeneralController().setCurrentEstudent(null);
        App.setRoot("role-selection");
    }

    @FXML
    private void onExit() {
        App.exitApplication();
    }

    private void refreshCourseCards() {
        if (coursesListController == null) {
            return;
        }
        List<Course> courses = App.getGeneralController()
                .getCoursesForCurrentStudent(showingEnrolledCourses);
        String search = txtCourseSearch.getText() == null
                ? "" : txtCourseSearch.getText().trim().toLowerCase(Locale.ROOT);
        List<Course> filteredCourses = courses.stream()
                .filter(course -> search.isEmpty()
                        || matchesSearch(course, search))
                .collect(Collectors.toList());

        coursesListController.DrawCoursesList(filteredCourses);
        boolean hasCourses = !courses.isEmpty();
        lblEmptyCourses.setVisible(filteredCourses.isEmpty());
        lblEmptyCourses.setManaged(filteredCourses.isEmpty());
        lblEmptyCourses.setText(hasCourses
                ? App.getMessages().getString("catalog.search.noResults")
                : App.getMessages().getString(showingEnrolledCourses
                        ? "catalog.myCourses.empty" : "catalog.available.empty"));
        lblCourseCount.setText(MessageFormat.format(
                App.getMessages().getString("menu.course.count"),
                App.getGeneralController().getCoursesForCurrentStudent(true).size()));
        lblCatalogTitle.setText(App.getMessages().getString(showingEnrolledCourses
                ? "catalog.myCourses.title" : "catalog.available.title"));
        btnCatalog.getStyleClass().setAll("boton-dibujado");
        btnMyCourses.getStyleClass().setAll("boton-dibujado");
    }

    private void refreshStatistics() {
        List<Course> enrolledCourses = App.getGeneralController()
                .getCoursesForCurrentStudent(true);
        int completedCount = 0;
        double totalProgress = 0;
        PieChart.Data completedSlice = new PieChart.Data(
                App.getMessages().getString("statistics.completed"), 0);
        PieChart.Data inProgressSlice = new PieChart.Data(
                App.getMessages().getString("statistics.inProgress"), 0);
        XYChart.Series<String, Number> progressSeries = new XYChart.Series<>();

        for (Course course : enrolledCourses) {
            double progress = App.getGeneralController()
                    .getCurrentStudentCourseProgressPercent(course.getId());
            totalProgress += progress;
            if (App.getGeneralController().isCurrentStudentCourseComplete(course.getId())) {
                completedCount++;
                completedSlice.setPieValue(completedSlice.getPieValue() + 1);
            } else {
                inProgressSlice.setPieValue(inProgressSlice.getPieValue() + 1);
            }
            progressSeries.getData().add(new XYChart.Data<>(course.getTitle(), progress));
        }

        double averageProgress = enrolledCourses.isEmpty()
                ? 0 : totalProgress / enrolledCourses.size();
        lblStatisticsEnrolled.setText(Integer.toString(enrolledCourses.size()));
        lblStatisticsCompleted.setText(Integer.toString(completedCount));
        lblStatisticsAverage.setText(String.format(Locale.ROOT, "%.0f%%", averageProgress));
        lblStatisticsEmpty.setVisible(enrolledCourses.isEmpty());
        lblStatisticsEmpty.setManaged(enrolledCourses.isEmpty());
        courseCompletionChart.getData().clear();
        courseProgressChart.getData().clear();
        if (!enrolledCourses.isEmpty()) {
            courseCompletionChart.getData().addAll(completedSlice, inProgressSlice);
            progressSeries.setName(App.getMessages().getString("statistics.progress"));
            courseProgressChart.getData().add(progressSeries);
        }
        updateChartView();
    }

    private void updateChartView() {
        courseProgressChart.setVisible(!showingPieChart);
        courseProgressChart.setManaged(!showingPieChart);
        courseCompletionChart.setVisible(showingPieChart);
        courseCompletionChart.setManaged(showingPieChart);
        lblChartTitle.setText(App.getMessages().getString(
                showingPieChart ? "statistics.completion" : "statistics.courseProgress"));
        btnToggleChart.setText(App.getMessages().getString(
                showingPieChart ? "statistics.showBars" : "statistics.showPie"));
    }

    private boolean matchesSearch(Course course, String search) {
        return contains(course.getTitle(), search)
                || contains(course.getDescription(), search)
                || contains(course.getId(), search);
    }

    private boolean contains(String value, String search) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(search);
    }

    private void showCourseDetails(Course course) {
        selectedCourse = course;
        selectedCourseIsEnrolled = App.getGeneralController().getCurrentEstudent()
                .isRegisterOnCourse(course.getId());
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
        nodeDetailsPanel.getStyleClass().setAll("node-details-panel");

        btnEnroll.setVisible(!selectedCourseIsEnrolled);
        btnEnroll.setManaged(!selectedCourseIsEnrolled);
        btnCompleteLesson.setVisible(false);
        btnCompleteLesson.setManaged(false);
        boolean courseCompleted = selectedCourseIsEnrolled
                && App.getGeneralController().isCurrentStudentCourseComplete(course.getId());
        lblEnrollmentMessage.setVisible(selectedCourseIsEnrolled);
        lblEnrollmentMessage.setManaged(selectedCourseIsEnrolled);
        lblEnrollmentMessage.setText(courseCompleted
                ? App.getMessages().getString("catalog.course.completed")
                : selectedCourseIsEnrolled
                        ? App.getMessages().getString("catalog.lesson.selectPrompt") : "");
        if (selectedCourseIsEnrolled && !courseCompleted) {
            String currentLessonId = App.getGeneralController().getCurrentLessonId(course.getId());
            courseTreeView.setHighlightedNodeId(currentLessonId);
        } else {
            showNodeDetails(null);
        }
        catalogView.setVisible(false);
        catalogView.setManaged(false);
        courseDetailView.setVisible(true);
        courseDetailView.setManaged(true);
    }

    private void showNodeDetails(TreeNode<EducativeElement> node) {
        if (node == null || node.getData() == null) {
            lblNodeTitle.setText(App.getMessages().getString("catalog.node.select"));
            lblNodeType.setText("");
            lblNodeDescription.setText("");
            btnCompleteLesson.setVisible(false);
            btnCompleteLesson.setManaged(false);
            return;
        }

        EducativeElement element = node.getData();
        lblNodeTitle.setText(element.getTitle() == null
                ? element.getId() : element.getTitle());
        lblNodeType.setText(App.getMessages().getString(nodeTypeMessageKey(element.getElementType())));
        String nodeDescription = element.getDescription() == null || element.getDescription().isBlank()
                ? App.getMessages().getString("catalog.course.noDescription")
                : element.getDescription();
        if (element instanceof Lessons) {
            nodeDescription += System.lineSeparator() + MessageFormat.format(
                    App.getMessages().getString("catalog.node.duration"),
                    ((Lessons) element).getDuration());
        }
        lblNodeDescription.setText(nodeDescription);

        boolean isCurrentLesson = selectedCourseIsEnrolled
                && element.getElementType() == EducativeElementType.LESSON
                && element.getId().equals(App.getGeneralController()
                        .getCurrentLessonId(selectedCourse.getId()))
                && !App.getGeneralController().isCurrentStudentCourseComplete(selectedCourse.getId());
        btnCompleteLesson.setVisible(isCurrentLesson);
        btnCompleteLesson.setManaged(isCurrentLesson);
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

    private void updateEnrollmentState(String message, boolean hideEnrollmentButton) {
        btnEnroll.setVisible(!hideEnrollmentButton);
        btnEnroll.setManaged(!hideEnrollmentButton);
        lblEnrollmentMessage.setVisible(true);
        lblEnrollmentMessage.setManaged(true);
        lblEnrollmentMessage.setText(message);
    }

    private void refreshCourseCount() {
        int count = App.getGeneralController().getCoursesForCurrentStudent(true).size();
        lblCourseCount.setText(MessageFormat.format(
                App.getMessages().getString("menu.course.count"), count));
    }
}
