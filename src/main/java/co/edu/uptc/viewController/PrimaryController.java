package co.edu.uptc.viewController;

import java.io.IOException;
import javafx.fxml.FXML;
import co.edu.uptc.view.App;
public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
