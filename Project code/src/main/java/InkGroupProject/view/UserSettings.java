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
        root.add(goBack, 0, 6);

        goBack.setOnAction(e -> {
            UserPage userPage = new UserPage();
            userPage.start(App.getStage());
        });
    }

    private void initUpdatePassword() {
        // -- Update password --
        Label updatePasswordLabel = new Label("Update password");
        root.add(updatePasswordLabel, 0, 0);

        // Current password
        final TextField currentPassword = new PasswordField();
        currentPassword.setPromptText("Enter current password");
        currentPassword.setFocusTraversable(false);
        root.add(currentPassword, 0, 1);

        // New password
        final TextField newPassword = new PasswordField();
        newPassword.setPromptText("Enter new password");
        newPassword.setFocusTraversable(false);
        root.add(newPassword, 0, 2);

        // Repeat new password
        final TextField repeatNewPassword = new PasswordField();
        repeatNewPassword.setPromptText("Re-enter new password");
        repeatNewPassword.setFocusTraversable(false);
        root.add(repeatNewPassword, 0, 3);

        // Update password button
        Button updatePasswordButton = new Button("Update password");
        updatePasswordButton.setFocusTraversable(false);
        root.add(updatePasswordButton, 0, 4);

        // User feedback label
        final Label infoLabel = new Label();
        root.add(infoLabel, 0, 5, 2, 1);

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
        root.add(updateUserInfo, 1, 0);

        // First name
        final TextField firstNameField = new TextField();
        firstNameField.setPromptText("Enter your first name");
        firstNameField.setFocusTraversable(false);
        root.add(firstNameField, 1, 1);

        // Last name
        final TextField lastNameField = new TextField();
        lastNameField.setPromptText("Enter your last name");
        lastNameField.setFocusTraversable(false);
        root.add(lastNameField, 1, 2);

        // Email address
        final TextField emailField = new TextField();
        emailField.setPromptText("Enter your email address");
        emailField.setFocusTraversable(false);
        root.add(emailField, 1, 3);

        // Update info button
        Button updateInfoButton = new Button("Update info");
        updateInfoButton.setFocusTraversable(false);
        root.add(updateInfoButton, 1, 4);

        // Update TextFields with user's information
        if (currentUser.isLoggedIn()) {
            firstNameField.setText(currentUser.getFirstName());
            lastNameField.setText(currentUser.getLastName());
            emailField.setText(currentUser.getEmail());
        }

        // User feedback label
        final Label infoLabel = new Label();
        root.add(infoLabel, 1, 5, 2, 1);

        updateInfoButton.setOnAction(e -> {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String email = emailField.getText();
            boolean emailTaken = db.getAccount(email) != null && !email.equals(currentUser.getEmail());

            if ((firstName.isEmpty() || lastName.isEmpty() || email.isEmpty())) {
                infoLabel.setTextFill(Color.RED);
                infoLabel.setText("Text fields cannot be empty.");
            } else if (emailTaken) {
                infoLabel.setTextFill(Color.RED);
                infoLabel.setText("Email address already in use.");
            } else {
                db.updateUserInfo(firstName, lastName, email, currentUser.getId());
                currentUser.updateUserInfo(firstName, lastName, email);
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
