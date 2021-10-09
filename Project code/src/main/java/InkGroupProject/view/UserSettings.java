package InkGroupProject.view;

import InkGroupProject.model.Database;
import InkGroupProject.model.UserSession;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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
        GridPane.setConstraints(goBack, 0, 5);
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
