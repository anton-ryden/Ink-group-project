package InkGroupProject.view;

import InkGroupProject.controller.World;
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
    private GridPane grid;
    private Database db;

    public LoginPage() {
        init();
    }

    private void init() {
        db = Database.getInstance(":resource:InkGroupProject/db/database.db");

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

        Hyperlink createAccount = new Hyperlink("Create an account");
        createAccount.setFocusTraversable(false);
        GridPane.setConstraints(createAccount, 0, 2);
        grid.getChildren().add(createAccount);

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

                    // Create user session
                    User account = db.getAccount(email.getText());
                    UserSession.login(account);
                    World worldMap = WorldBuilder.create()
                            .resolution(World.Resolution.HI_RES)
                            .zoomEnabled(true)
                            .hoverEnabled(true)
                            .selectionEnabled(true)
                            .fadeColors(true)
                            .build();
                    InkGroupProject.view.Map mapScene = new Map(worldMap);
                    worldMap.setView(mapScene);
                    worldMap.initDonations();

                    worldMap.addPropertyChangeListener(mapScene);
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

        email.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.TAB)
                password.requestFocus();
        });

        password.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.TAB && e.isShiftDown())
                email.requestFocus();
        });
    }

    public void start(Stage stage) {
        Scene loginScene = new Scene(grid);
        stage.setTitle("Login");
        stage.setResizable(false);
        stage.setScene(loginScene);
        stage.show();
    }

    public Parent getRoot() {
        return grid;
    }

}
