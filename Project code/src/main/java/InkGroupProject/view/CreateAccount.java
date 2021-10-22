package InkGroupProject.view;

import InkGroupProject.model.Database;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CreateAccount implements IScene {
    private GridPane root;
    private Database db;

    public CreateAccount() {
        init();
    }

    private void init() {
        db = Database.getInstance(":resource:InkGroupProject/db/database.db");

        // GridPane container
        root = new GridPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setVgap(5);
        root.setHgap(5);

        // First name text field
        final TextField firstName = new TextField();
        firstName.setPromptText("Enter your first name");
        firstName.setFocusTraversable(false);
        root.add(firstName, 0, 0);

        // Last name text field
        final TextField lastName = new TextField();
        lastName.setPromptText("Enter your last name");
        lastName.setFocusTraversable(false);
        root.add(lastName, 1, 0);

        // Email text field
        final TextField email = new TextField();
        email.setPromptText("Enter your email address");
        email.setFocusTraversable(false);
        root.add(email, 0, 1, 2, 1);

        // Password text field
        final TextField password = new PasswordField();
        password.setPromptText("Enter your password");
        password.setFocusTraversable(false);
        root.add(password, 0, 2);

        // Re-enter password text field
        final TextField rePassword = new PasswordField();
        rePassword.setPromptText("Re-enter your password");
        rePassword.setFocusTraversable(false);
        root.add(rePassword, 1, 2);

        // Login button
        Button createAccount = new Button(" Create\nAccount");
        createAccount.setFocusTraversable(false);
        createAccount.setDefaultButton(true);
        GridPane.setConstraints(createAccount, 2, 0, 1, 2, HPos.CENTER, VPos.TOP);
        root.getChildren().add(createAccount);

        // Go back to login screen button
        Hyperlink goBack = new Hyperlink("<- Go back");
        goBack.setFocusTraversable(false);
        root.add(goBack, 0, 3);

        // User feedback label
        final Label infoLabel = new Label();
        root.add(infoLabel, 0, 4, 2, 1);

        // Click event for create account button
        createAccount.setOnAction(e -> {
            boolean isFirstNameEmpty = firstName.getText().isEmpty();
            boolean isLastNameEmpty = lastName.getText().isEmpty();
            boolean isEmailEmpty = email.getText().isEmpty();
            boolean isPasswordEmpty = password.getText().isEmpty();
            boolean isRePasswordEmpty = rePassword.getText().isEmpty();
            if (isFirstNameEmpty || isLastNameEmpty || isEmailEmpty || isPasswordEmpty || isRePasswordEmpty) {
                infoLabel.setTextFill(Color.RED);
                infoLabel.setText("Please fill out all the information above.");
            } else {
                if (!password.getText().equals(rePassword.getText())) {
                    infoLabel.setTextFill(Color.RED);
                    infoLabel.setText("Passwords do not match.");
                } else if (db.createAccount(firstName.getText(), lastName.getText(), email.getText(), password.getText())) {
                    infoLabel.setTextFill(Color.GREEN);
                    infoLabel.setText("Account created successfully!");
                    LoginPage loginPage = new LoginPage();
                    loginPage.start(App.getStage());
                } else {
                    infoLabel.setTextFill(Color.RED);
                    infoLabel.setText("Account already exists.");
                }
            }
        });

        // Click event for go back button
        goBack.setOnAction(e -> {
            goBack.setVisited(false);
            LoginPage loginPage = new LoginPage();
            loginPage.start(App.getStage());
        });

        // Move to next TextField on TAB
        firstName.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.TAB)
                lastName.requestFocus();
        });

        // Move to next TextField on TAB, move to previous TextField on TAB & SHIFT combined
        lastName.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.TAB) {
                if (e.isShiftDown())
                    firstName.requestFocus();
                else
                    email.requestFocus();
            }
        });

        // Move to next TextField on TAB, move to previous TextField on TAB & SHIFT combined
        email.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.TAB) {
                if (e.isShiftDown())
                    lastName.requestFocus();
                else
                    password.requestFocus();
            }
        });

        // Move to next TextField on TAB, move to previous TextField on TAB & SHIFT combined
        password.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.TAB) {
                if (e.isShiftDown())
                    email.requestFocus();
                else
                    rePassword.requestFocus();
            }
        });

        // Move to next TextField on TAB, move to previous TextField on TAB & SHIFT combined
        rePassword.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.TAB && e.isShiftDown())
                password.requestFocus();
        });
    }

    public void start(Stage stage) {
        Scene createAccountScene = new Scene(root);
        stage.setTitle("Create account");
        stage.setResizable(false);
        stage.setScene(createAccountScene);
        stage.show();
    }

    public Parent getRoot() {
        return root;
    }
}
