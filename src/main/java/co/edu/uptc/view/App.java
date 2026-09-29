package co.edu.uptc.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import co.edu.uptc.controller.GeneralController;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;
    private static Locale locale = Locale.forLanguageTag("es");
    private static String currentView = "role-selection";
    private static GeneralController generalController;

    @Override
    public void start(Stage stage) throws IOException {
        generalController = new GeneralController();
        scene = new Scene(loadFXML(currentView), 900, 640);
        primaryStage = stage;
        primaryStage.setTitle(getMessages().getString("app.title"));
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setFullScreenExitHint(getMessages().getString("app.fullscreen.exitHint"));
        stage.setFullScreen(true);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        Parent root = loadFXML(fxml);
        scene.setRoot(root);
        currentView = fxml;
    }

    public static void setLocale(Locale newLocale) throws IOException {
        locale = newLocale;
        scene.setRoot(loadFXML(currentView));
        primaryStage.setTitle(getMessages().getString("app.title"));
    }

    public static ResourceBundle getMessages() {
        return ResourceBundle.getBundle("co.edu.uptc.i18n.messages", locale);
    }

    public static GeneralController getGeneralController() {
        return generalController;
    }

    public static void exitApplication() {
        Platform.exit();
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                App.class.getResource("/co/edu/uptc/fxml/" + fxml + ".fxml"),
                getMessages());
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}