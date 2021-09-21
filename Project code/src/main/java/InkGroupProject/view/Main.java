package InkGroupProject.view;

import javafx.geometry.Pos;
import  javafx.scene.control.Label;
import InkGroupProject.model.WorldBuilder;
import InkGroupProject.model.World;
import InkGroupProject.model.World.Resolution;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.geometry.Insets;

import java.awt.*;


public class Main extends Application {
    private World         worldMap;
    private HBox root;

    @Override public void init() {

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

    @Override public void start(Stage stage) {
        Scene scene = new Scene(root);
        stage.setTitle("Interactive map");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
