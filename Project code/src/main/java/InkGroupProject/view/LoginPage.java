package InkGroupProject.view;

import InkGroupProject.model.*;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LoginPage implements IScene {
    private GridPane root;
    private Database db;

    public LoginPage() {
        init();
    }

    private void init() {
//        db = Database.getInstance(":resource:InkGroupProject/db/database.db");
        db = Database.getInstance(":resource:main/resources/InkGroupProject/db/database.db");

        // GridPane container
        root = new GridPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setVgap(5);
        root.setHgap(5);

        // Email text field
        final TextField email = new TextField();
        email.setPromptText("Enter your email address");
        email.setFocusTraversable(false);
        email.setPrefColumnCount(16);
        root.add(email, 0, 0);

        // Password text field
        final TextField password = new PasswordField();
        password.setPromptText("Enter your password");
        password.setFocusTraversable(false);
        root.add(password, 0, 1);

        // Login button
        Button login = new Button("Login");
        login.setFocusTraversable(false);
        login.setDefaultButton(true);
        root.add(login, 1, 0);

        Hyperlink createAccount = new Hyperlink("Create an account");
        createAccount.setFocusTraversable(false);
        root.add(createAccount, 0, 2);

        final Label infoLabel = new Label();
        root.add(infoLabel, 0, 3, 2, 1);

        login.setOnAction(e -> {
            if (email.getText().isEmpty() || password.getText().isEmpty()) {
                infoLabel.setTextFill(Color.RED);
                infoLabel.setText("Please enter your email and password.");
            } else {
                if (db.login(email.getText(), password.getText())) {
                    infoLabel.setTextFill(Color.GREEN);
                    infoLabel.setText("Login successful!");

                    // Create user session
                    User account = db.getAccount(email.getText());
                    UserSession.login(account);

                    Map mapScene = new Map();
                    mapScene.start(Main.getStage());
                } else {
                    infoLabel.setTextFill(Color.RED);
                    infoLabel.setText("Invalid email or password.");
                }
            }
        });

        createAccount.setOnAction(e -> {
            createAccount.setVisited(false);
            CreateAccount createAccountScene = new CreateAccount();
            createAccountScene.start(Main.getStage());
        });

        // Move to next TextField on TAB
        email.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.TAB)
                password.requestFocus();
        });

        // Move to previous TextField on TAB & SHIFT combined
        password.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.TAB && e.isShiftDown())
                email.requestFocus();
        });
    }

    public void start(Stage stage) {
        Scene loginScene = new Scene(root);
        stage.setTitle("Login");
        stage.setResizable(false);
        stage.setScene(loginScene);
        stage.show();
    }

    public Parent getRoot() {
        return root;
    }
}
