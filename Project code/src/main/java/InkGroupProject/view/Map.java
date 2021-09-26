package InkGroupProject.view;

import InkGroupProject.model.UserSession;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import InkGroupProject.model.WorldBuilder;
import InkGroupProject.model.World;
import InkGroupProject.model.World.Resolution;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.geometry.Insets;

public class Map extends Parent implements IScene {
    private World worldMap;
    private GridPane root;

    public Map() {
        init();
    }

    public void init() {
        worldMap = WorldBuilder.create()
                .resolution(Resolution.HI_RES)
                .zoomEnabled(true)
                .hoverEnabled(true)
                .selectionEnabled(true)
                .fadeColors(true)
                .build();

        root = new GridPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setAlignment(Pos.CENTER);

        Button prevBtn = new Button("Previous");
        Button nextBtn = new Button("Next");

        // Testing UserSession
        UserSession user = UserSession.getInstance();
        Label welcome = new Label();
        welcome.setText("Welcome, " + user.getFirstName());
        root.add(welcome, 0, 1);
        // End testing UserSession

        root.add(prevBtn, 0,0);
        root.add(worldMap, 1,0);
        root.add(nextBtn, 2,0);
        GridPane.setHgrow(worldMap, Priority.ALWAYS);
        GridPane.setVgrow(worldMap, Priority.ALWAYS);
    }

    public void start(Stage stage) {
        Scene mapScene = new Scene(root);
        stage.setTitle("Interactive Map");
        stage.setResizable(true);
        stage.setScene(mapScene);
        stage.show();
    }

    public Parent getRoot() {
        return root;
    }
}
