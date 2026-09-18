package co.edu.uptc.viewController;

import java.io.IOException;
import javafx.fxml.FXML;
import co.edu.uptc.view.App;
public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}