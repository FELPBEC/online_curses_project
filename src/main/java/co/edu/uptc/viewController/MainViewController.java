package co.edu.uptc.viewController;

import co.edu.uptc.controller.GeneralController;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;

public class MainViewController {
    private GeneralController controller;

    @FXML
    private void initialize() {
        controller=new GeneralController();
    }

    @FXML
    private void goToStudentLogin(ActionEvent event) {
    }
}
