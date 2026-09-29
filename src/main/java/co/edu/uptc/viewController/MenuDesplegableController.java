package co.edu.uptc.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox; // 1. Cambiado de VBox a HBox
import co.edu.uptc.controller.GeneralController;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.util.Duration;

public class MenuDesplegableController {
    
    @FXML
    private Button btnToggleMenu;
    private TranslateTransition animacion;
    private boolean menuAbierto = false;
    
    @FXML
    private HBox menuHoja; // 1. Coincide con el FXML

    private GeneralController generalController;
    
    @FXML
    private Button btnLookMyCourses;
    @FXML
    private Button btnSearchNewCourses;
    @FXML
    private Button btnCreateCourse;
    @FXML
    private Button btnManageCourses;
    @FXML
    private Button btnLogOut;

    // 2. El initialize debe estar vacío sin argumentos
    @FXML
    private void initialize() {
        // Debes inicializar tu GeneralController aquí o llamarlo desde el Singleton
        // Ej: this.generalController = new GeneralController(); o Sesion.getInstancia().getController();
        
        menuHoja.setTranslateY(260);
        
    }
    public void setGeneralController(GeneralController controller) {
        this.generalController = controller;

        // Ahora sí podemos validar porque el controlador ya llegó
        if (generalController.getCurrentTeacher() != null) {
            hideButton(btnLookMyCourses);
            hideButton(btnSearchNewCourses);
        }
        if (generalController.getCurrentEstudent() != null) {
            hideButton(btnCreateCourse);
            hideButton(btnManageCourses);
        }
    }
    // Al dejarlo 'public' no necesita @FXML y evitas el bug visual de VS Code
    public void toggleMenu() {
        if (animacion != null && animacion.getStatus() == javafx.animation.Animation.Status.RUNNING) {
            return; // Aquí es donde VS Code se confundía, ahora ya no lo hará
        }

        animacion = new TranslateTransition(Duration.millis(350), menuHoja);

        if (menuAbierto) {
            animacion.setToY(260);
            menuAbierto = false;
        } else {
            animacion.setToY(0);
            menuAbierto = true;
        }

        animacion.play();
    }

    private void hideButton(Button button) {
        button.setVisible(false);
        button.setManaged(false);
    }

    @FXML
    private void logOut(ActionEvent event) {
        if (generalController != null) {
            generalController.setCurrentTeacher(null);
            generalController.setCurrentEstudent(null);
        }
    }

    @FXML
    private void goToMyCourses(ActionEvent event) {
    }

    @FXML
    private void searchCourses(ActionEvent event) {
    }

    @FXML
    private void createCourses(ActionEvent event) {
    }

    @FXML
    private void manageCourses(ActionEvent event) {
    }
}