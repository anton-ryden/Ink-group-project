package InkGroupProject.view;

import InkGroupProject.model.Database;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LoginPage extends Parent implements IScene {
    private GridPane grid;
    private Database db;

    public LoginPage() {
        init();
    }

    public void init() {
        db = new Database(":resource:InkGroupProject/db/database.db");

        // GridPane container
        grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        // Email text field
        final TextField email = new TextField();
        email.setPromptText("Enter your email address");
        email.setFocusTraversable(false);
        email.setPrefColumnCount(16);
        GridPane.setConstraints(email, 0, 0);
        grid.getChildren().add(email);

        // Password text field
        final TextField password = new PasswordField();
        password.setPromptText("Enter your password");
        password.setFocusTraversable(false);
        GridPane.setConstraints(password, 0, 1);
        grid.getChildren().add(password);

        // Login button
        Button login = new Button("Login");
        login.setFocusTraversable(false);
        login.setDefaultButton(true);
        GridPane.setConstraints(login, 1, 0);
        grid.getChildren().add(login);

        final Label infoLabel = new Label();
        GridPane.setConstraints(infoLabel, 0, 3);
        GridPane.setColumnSpan(infoLabel, 2);
        grid.getChildren().add(infoLabel);


        login.setOnAction(e -> {
            if (email.getText().isEmpty() || password.getText().isEmpty()) {
                infoLabel.setTextFill(Color.RED);
                infoLabel.setText("Please enter your email and password.");
            } else {
                if (db.login(email.getText(), password.getText())) {
                    infoLabel.setTextFill(Color.GREEN);
                    infoLabel.setText("Login successful!");
                    Main app = new Main();
                    app.changeScene(new Map());
                } else {
                    infoLabel.setTextFill(Color.RED);
                    infoLabel.setText("Invalid email or password.");
                }
            }
        });
    }

    public Parent getRoot() {
        return grid;
    }

    public void start(Stage stage) {
        Scene loginScene = new Scene(grid);
        stage.setTitle("Login");
        stage.setScene(loginScene);
        stage.show();
    }
}
