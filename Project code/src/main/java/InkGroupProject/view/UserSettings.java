package InkGroupProject.view;

import InkGroupProject.model.Database;
import InkGroupProject.model.UserSession;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class UserSettings implements IScene {
    private GridPane root;
    private Database db;
    private UserSession currentUser;

    public UserSettings() {
        init();
        initUpdatePassword();
        initUpdateUserInfo();
    }

    private void init() {
        db = Database.getInstance(":resource:InkGroupProject/db/database.db");
        currentUser = UserSession.getInstance();

        root = new GridPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setVgap(5);
        root.setHgap(5);

        // Go back to the User page
        Hyperlink goBack = new Hyperlink("<- Go back");
        goBack.setFocusTraversable(false);
        GridPane.setConstraints(goBack, 0, 6);
        root.getChildren().add(goBack);
    }

    private void initUpdatePassword() {
        // -- Update password --
        Label updatePasswordLabel = new Label("Update password");
        GridPane.setConstraints(updatePasswordLabel, 0, 0);
        root.getChildren().add(updatePasswordLabel);

        // Current password
        final TextField currentPassword = new PasswordField();
        currentPassword.setPromptText("Enter current password");
        currentPassword.setFocusTraversable(false);
        GridPane.setConstraints(currentPassword, 0, 1);
        root.getChildren().add(currentPassword);

        // New password
        final TextField newPassword = new PasswordField();
        newPassword.setPromptText("Enter new password");
        newPassword.setFocusTraversable(false);
        GridPane.setConstraints(newPassword, 0, 2);
        root.getChildren().add(newPassword);

        // Repeat new password
        final TextField repeatNewPassword = new PasswordField();
        repeatNewPassword.setPromptText("Re-enter new password");
        repeatNewPassword.setFocusTraversable(false);
        GridPane.setConstraints(repeatNewPassword, 0, 3);
        root.getChildren().add(repeatNewPassword);

        // Update password button
        Button updatePasswordButton = new Button("Update password");
        updatePasswordButton.setFocusTraversable(false);
//        updatePasswordButton.setDefaultButton(true);
        GridPane.setConstraints(updatePasswordButton, 0, 4);
        root.getChildren().add(updatePasswordButton);

        // User feedback label
        final Label infoLabel = new Label();
        GridPane.setConstraints(infoLabel, 0, 5);
        GridPane.setColumnSpan(infoLabel, 2);
        root.getChildren().add(infoLabel);

        updatePasswordButton.setOnAction(e -> {
            boolean isCurrentPasswordEmpty = currentPassword.getText().isEmpty();
            boolean isNewPasswordEmpty = newPassword.getText().isEmpty();
            boolean isRepeatNewPasswordEmpty = repeatNewPassword.getText().isEmpty();
            if (isCurrentPasswordEmpty || isNewPasswordEmpty || isRepeatNewPasswordEmpty) {
                infoLabel.setTextFill(Color.RED);
                infoLabel.setText("Please fill out all the information above.");
            } else {
                if (!db.login(currentUser.getEmail(), currentPassword.getText())) {
                    infoLabel.setTextFill(Color.RED);
                    infoLabel.setText("Wrong current password.");
                } else if (currentPassword.getText().equals(newPassword.getText())) {
                    infoLabel.setTextFill(Color.RED);
                    infoLabel.setText("Can't change to same password.");
                } else if (!newPassword.getText().equals(repeatNewPassword.getText())) {
                    infoLabel.setTextFill(Color.RED);
                    infoLabel.setText("Passwords do not match.");
                } else {
                    db.updatePassword(newPassword.getText(), UserSession.getInstance().getId());
                    infoLabel.setTextFill(Color.GREEN);
                    infoLabel.setText("Password updated.");
                }
            }

        } );
    }

    private void initUpdateUserInfo() {
        // -- Update user info --
        Label updateUserInfo = new Label("Update user information");
        GridPane.setConstraints(updateUserInfo, 1, 0);
        root.getChildren().add(updateUserInfo);

        // First name
        final TextField firstName = new TextField();
        firstName.setPromptText("Enter your first name");
        firstName.setFocusTraversable(false);
        GridPane.setConstraints(firstName, 1, 1);
        root.getChildren().add(firstName);

        // Last name
        final TextField lastName = new TextField();
        lastName.setPromptText("Enter your last name");
        lastName.setFocusTraversable(false);
        GridPane.setConstraints(lastName, 1, 2);
        root.getChildren().add(lastName);

        // Email address
        final TextField email = new TextField();
        email.setPromptText("Enter your email address");
        email.setFocusTraversable(false);
        GridPane.setConstraints(email, 1, 3);
        root.getChildren().add(email);

        // Update info button
        Button updateInfoButton = new Button("Update info");
        updateInfoButton.setFocusTraversable(false);
//        updateInfoButton.setDefaultButton(true);
        GridPane.setConstraints(updateInfoButton, 1, 4);
        root.getChildren().add(updateInfoButton);

        // Update textfields with user's information
        if (currentUser.isLoggedIn()) {
            firstName.setText(currentUser.getFirstName());
            lastName.setText(currentUser.getLastName());
            email.setText(currentUser.getEmail());
        }

        // User feedback label
        final Label infoLabel = new Label();
        GridPane.setConstraints(infoLabel, 1, 5);
        GridPane.setColumnSpan(infoLabel, 2);
        root.getChildren().add(infoLabel);

        updateInfoButton.setOnAction(e -> {
            boolean isFirstNameEmpty = firstName.getText().isEmpty();
            boolean isLastNameEmpty = lastName.getText().isEmpty();
            boolean isEmailEmpty = email.getText().isEmpty();
            if ((isFirstNameEmpty || isLastNameEmpty || isEmailEmpty)) {
                infoLabel.setTextFill(Color.RED);
                infoLabel.setText("Please insert new info");
            } else {
                db.updateUserInfo(firstName.getText(), lastName.getText(), email.getText(), UserSession.getInstance().getId());
                infoLabel.setTextFill(Color.GREEN);
                infoLabel.setText("Information updated.");
            }
        });
    }

    @Override
    public void start(Stage stage) {
        Scene userSettingsScene = new Scene(root);
        stage.setTitle("User settings");
        stage.setResizable(false);
        stage.setScene(userSettingsScene);
        stage.show();
    }

    @Override
    public Parent getRoot() {
        return root;
    }
}
