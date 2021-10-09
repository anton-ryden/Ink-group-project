package InkGroupProject.view;

import InkGroupProject.model.Database;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class UserSettings implements IScene {
    private GridPane root;
    private Database db;

    public UserSettings() {
        init();
    }

    private void init() {
        db = Database.getInstance(":resource:InkGroupProject/db/database.db");

        root = new GridPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setVgap(5);
        root.setHgap(5);
    }

    @Override
    public void start(Stage stage) {
        Scene userSettingsScene = new Scene(root);
        stage.setTitle("User Settings");
        stage.setResizable(false);
        stage.setScene(userSettingsScene);
        stage.show();
    }

    @Override
    public Parent getRoot() {
        return root;
    }
}
