package InkGroupProject.view;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import InkGroupProject.model.WorldBuilder;
import InkGroupProject.model.World;
import InkGroupProject.model.World.Resolution;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.geometry.Insets;

public class Map extends Parent implements IScene {
    private World worldMap;
    private HBox  root;

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

        root = new HBox(5);
        root.setPadding(new Insets(5));
        root.setAlignment(Pos.BASELINE_RIGHT);

        Button prevBtn = new Button("Previous");
        Button nextBtn = new Button("Next");

        root.getChildren().addAll(prevBtn, worldMap, nextBtn);
    }

    public void start(Stage stage) {
        Scene loginScene = new Scene(root);
        stage.setTitle("Interactive Map");
        stage.setScene(loginScene);
        stage.show();
    }

    public Parent getRoot() {
        return root;
    }
}
