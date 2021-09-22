package InkGroupProject.view;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage primaryStage) {
        stage = primaryStage;
        LoginPage loginPage = new LoginPage();
        Scene loginScene = new Scene(loginPage.getRoot());
        primaryStage.setTitle("Login");
        primaryStage.setResizable(false);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public void changeScene(IScene scene) {
        stage.getScene().setRoot((Parent)scene);
        scene.start(stage);
    }

    @Override public void stop() {
        System.exit(0);
    }
}
